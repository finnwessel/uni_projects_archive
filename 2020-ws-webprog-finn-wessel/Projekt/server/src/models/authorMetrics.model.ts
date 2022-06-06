import { Column, Entity, JoinColumn, OneToOne, PrimaryColumn } from 'typeorm';
import { Author } from './author.model';
import { Exclude } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class AuthorMetrics {
  @Exclude()
  @PrimaryColumn({ type: 'varchar', length: 50, unique: true })
  id: string;

  @OneToOne(() => Author, (author) => author.public_metrics)
  @JoinColumn({ name: 'id' })
  author: string;

  @ApiProperty({ example: 420, description: 'Follower count the author' })
  @Column('int')
  followers_count: number;

  @ApiProperty({ example: 240, description: 'Following count the author' })
  @Column('int')
  following_count: number;

  @ApiProperty({ example: 444, description: 'Tweet count the author' })
  @Column('int')
  tweet_count: number;

  @ApiProperty({ example: 3, description: 'Listed count the author' })
  @Column('int')
  listed_count: number;
}
