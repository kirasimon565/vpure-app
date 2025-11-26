import jwt from 'jsonwebtoken';
import { users } from '../db/schema';
import { env } from '../env';

export function generateToken(user: typeof users.$inferSelect) {
    const payload = {
        id: user.id,
        email: user.email,
        name: user.name,
    };
    return jwt.sign(payload, env.JWT_SECRET, { expiresIn: '1d' });
}

export function verifyToken<T>(token: string): T | null {
    try {
        return jwt.verify(token, env.JWT_SECRET) as T;
    } catch (e) {
        return null;
    }
}
