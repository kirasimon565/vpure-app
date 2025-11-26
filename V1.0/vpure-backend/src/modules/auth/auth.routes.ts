import { FastifyInstance } from 'fastify';
import { loginUserController, registerUserController } from './auth.controller';
import { loginUserJsonSchema, registerUserJsonSchema } from './auth.schemas';

export async function authRoutes(app: FastifyInstance) {
  app.post(
    '/register',
    { schema: registerUserJsonSchema },
    registerUserController
  );

  app.post(
    '/login',
    { schema: loginUserJsonSchema },
    loginUserController
  );
}
