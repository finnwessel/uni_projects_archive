export const config = () => ({
  database: {
    type: 'mysql',
    host: process.env.DATABASE_HOST,
    port: process.env.DATABASE_PORT,
    username: process.env.DATABASE_USER,
    password: process.env.DATABASE_PASS,
    database: process.env.DATABASE_NAME,
    entities: ['dist/**/models/*.model{.ts,.js}'],
    migrations: ['dist/migrations/**/*.js'],
    synchronize: process.env.DATABASE_SYNC,
    cli: {
      migrationsDir: 'src/db/migrations',
      entitiesDir: 'src/db/entities',
    },
    charset: 'utf8mb4_unicode_ci',
    migrationsTableName: 'migrations',
    migrationsRun: true,
    autoSchemaSync: true,
    autoLoadEntities: true,
  },
});
