import { sqliteTable, text, integer, primaryKey } from 'drizzle-orm/sqlite-core';
export const devices = sqliteTable('devices', { code:text('code').primaryKey(), secretHash:text('secret_hash').notNull(), csrf:text('csrf').notNull(), phrase:text('phrase').notNull(), userId:text('user_id'), display:text('display'), expires:integer('expires').notNull(), used:integer('used').notNull().default(0) });
export const tokens = sqliteTable('tokens', { hash:text('hash').primaryKey(), userId:text('user_id').notNull(), display:text('display').notNull(), expires:integer('expires').notNull() });
export const entries = sqliteTable('entries', { userId:text('user_id').notNull(), id:text('id').notNull(), kind:text('kind').notNull(), body:text('body').notNull(), version:integer('version').notNull(), updated:integer('updated').notNull() },t=>[primaryKey({columns:[t.userId,t.id]})]);
export const shares = sqliteTable('shares', { id:text('id').primaryKey(), userId:text('user_id').notNull(), body:text('body').notNull(), created:integer('created').notNull() });
export const limits = sqliteTable('limits', { key:text('key').primaryKey(), n:integer('n').notNull(), expires:integer('expires').notNull() });
