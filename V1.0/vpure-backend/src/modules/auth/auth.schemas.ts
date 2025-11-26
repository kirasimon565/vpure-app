import { z } from 'zod';
import { zodToJsonSchema } from 'zod-to-json-schema';

const registerUserSchema = z.object({
  name: z.string().min(3, 'Name must be at least 3 characters long'),
  email: z.string().email('Invalid email format'),
  password: z.string().min(8, 'Password must be at least 8 characters long'),
});

export type RegisterUserInput = z.infer<typeof registerUserSchema>;
export const registerUserJsonSchema = {
  body: zodToJsonSchema(registerUserSchema, 'registerUserSchema'),
};


const loginUserSchema = z.object({
  email: z.string().email('Invalid email format'),
  password: z.string(),
});

export type LoginUserInput = z.infer<typeof loginUserSchema>;
export const loginUserJsonSchema = {
  body: zodToJsonSchema(loginUserSchema, 'loginUserSchema'),
};
