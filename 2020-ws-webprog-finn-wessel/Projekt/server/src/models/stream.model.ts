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
export class Stream {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ type: 'varchar' })
  keywords: string;

  @ManyToMany(() => Tweet, {
    cascade: true,
  })
  @JoinTable()
  tweets: Tweet[];

  @CreateDateColumn()
  created_at: Date;
}
