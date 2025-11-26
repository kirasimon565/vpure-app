import { FastifyReply, FastifyRequest } from 'fastify';
import { db } from '../../db/client';
import { users } from '../../db/schema';
import { hashPassword, verifyPassword } from '../../utils/hash';
import { generateToken } from '../../utils/jwt';
import { fail, success } from '../../utils/reply';
import { LoginUserInput, RegisterUserInput } from './auth.schemas';
import { eq } from 'drizzle-orm';

// ==============================
// REGISTER
// ==============================
export async function registerUserController(
  request: FastifyRequest<{ Body: RegisterUserInput }>,
  reply: FastifyReply
) {
  const { name, email, password } = request.body;

  try {
    // Check if user already exists
    const existingUser = await db.query.users.findFirst({
      where: eq(users.email, email),
    });

    if (existingUser) {
      return fail(reply, 'User with this email already exists', 409);
    }

    // Hash password
    const passwordHash = await hashPassword(password);

    // Insert new user
    const [newUser] = await db.insert(users).values({
      name,
      email,
      passwordHash,
    }).returning();

    const token = generateToken(newUser);

    return success(reply, { user: newUser, token }, 201);

  } catch (error) {
    console.error(error);
    return fail(reply, 'Could not create user', 500);
  }
}

// ==============================
// LOGIN
// ==============================
export async function loginUserController(
  request: FastifyRequest<{ Body: LoginUserInput }>,
  reply: FastifyReply
) {
  const { email, password } = request.body;

  try {
    const user = await db.query.users.findFirst({
      where: eq(users.email, email),
    });

    if (!user) {
      return fail(reply, 'Invalid email or password', 401);
    }

    // Compare password
    const isPasswordCorrect = await verifyPassword(password, user.passwordHash);

    if (!isPasswordCorrect) {
      return fail(reply, 'Invalid email or password', 401);
    }

    const token = generateToken(user);

    return success(reply, { user, token });

  } catch (error) {
    console.error(error);
    return fail(reply, 'Internal server error', 500);
  }
}
