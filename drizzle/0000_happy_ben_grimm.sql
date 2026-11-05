CREATE TABLE `devices` (
	`code` text PRIMARY KEY NOT NULL,
	`secret_hash` text NOT NULL,
	`csrf` text NOT NULL,
	`phrase` text NOT NULL,
	`user_id` text,
	`display` text,
	`expires` integer NOT NULL,
	`used` integer DEFAULT 0 NOT NULL
);
--> statement-breakpoint
CREATE TABLE `entries` (
	`user_id` text NOT NULL,
	`id` text NOT NULL,
	`kind` text NOT NULL,
	`body` text NOT NULL,
	`version` integer NOT NULL,
	`updated` integer NOT NULL,
	PRIMARY KEY(`user_id`, `id`)
);
--> statement-breakpoint
CREATE TABLE `limits` (
	`key` text PRIMARY KEY NOT NULL,
	`n` integer NOT NULL,
	`expires` integer NOT NULL
);
--> statement-breakpoint
CREATE TABLE `shares` (
	`id` text PRIMARY KEY NOT NULL,
	`user_id` text NOT NULL,
	`body` text NOT NULL,
	`created` integer NOT NULL
);
--> statement-breakpoint
CREATE TABLE `tokens` (
	`hash` text PRIMARY KEY NOT NULL,
	`user_id` text NOT NULL,
	`display` text NOT NULL,
	`expires` integer NOT NULL
);
