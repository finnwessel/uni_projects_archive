import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.enableCors();
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: false,
      skipUndefinedProperties: true,
      transform: true,
    }),
  );
  const options = new DocumentBuilder()
    .setTitle('Tweets Api')
    .setDescription('The Api for Tweets')
    .setVersion('1.0')
    .addTag('tweets-api')
    .build();
  const document = SwaggerModule.createDocument(app, options);
  SwaggerModule.setup('api-documentation', app, document);
  await app.listen(3000);
}
bootstrap();
