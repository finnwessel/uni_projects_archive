import {
  BeforeInsert,
  Column,
  Entity,
  JoinTable,
  ManyToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Search } from './search.model';
import { hash } from 'bcrypt';

@Entity()
export class User {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column('varchar')
  username: string;

  @Column('varchar')
  email: string;

  @Column('varchar')
  password: string;

  // Relations
  @ManyToMany(() => Search, {
    cascade: true,
  })
  @JoinTable()
  searches: Search[];

  @BeforeInsert() async hashPassword() {
    this.password = await hash(this.password, 10);
  }
}
