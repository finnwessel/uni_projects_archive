import {
  Column,
  CreateDateColumn,
  Entity,
  JoinTable,
  ManyToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Tweet } from './tweet.model';

@Entity()
export class Search {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ type: 'varchar' })
  keywords: string;

  @Column({ type: 'varchar' })
  keywordsUnified: string;

  @Column({ type: 'boolean', default: null, nullable: true })
  images: boolean;

  @Column({ type: 'boolean', default: null, nullable: true })
  videos: boolean;

  @Column({ type: 'boolean', default: null, nullable: true })
  retweets: boolean;

  @ManyToMany(() => Tweet, {
    cascade: true,
  })
  @JoinTable()
  tweets: Tweet[];

  @CreateDateColumn()
  created_at: Date;
}
