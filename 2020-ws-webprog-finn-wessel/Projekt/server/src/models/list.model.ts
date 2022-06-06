import {
  Column,
  CreateDateColumn,
  Entity,
  JoinTable,
  ManyToMany,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
  VersionColumn,
} from 'typeorm';
import { Tweet } from './tweet.model';

@Entity()
export class List {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ type: 'varchar' })
  userId: string;

  @Column({ type: 'varchar' })
  name: string;

  @Column({ type: 'varchar' })
  sourceType: string;

  @Column({ type: 'uuid' })
  sourceId: string;

  @ManyToMany(() => Tweet, {
    cascade: true,
  })
  @JoinTable()
  tweets: Tweet[];

  @Column({ type: 'boolean', default: false })
  isPublic: boolean;

  @CreateDateColumn()
  created_at: Date;

  @UpdateDateColumn()
  updated_at: Date;

  @VersionColumn()
  version: number;
}
