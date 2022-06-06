import { Column, Entity, JoinColumn, OneToOne, PrimaryColumn } from 'typeorm';
import { Tweet } from './tweet.model';
import { Exclude } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class TweetMetrics {
  @Exclude()
  @PrimaryColumn({ type: 'varchar', length: 50, unique: true })
  id: string;

  @OneToOne(() => Tweet, (tweet) => tweet.public_metrics)
  @JoinColumn({ name: 'id' })
  tweet: string;

  @ApiProperty({ example: 123, description: 'Tweet retweet count' })
  @Column('int')
  retweet_count: number;

  @ApiProperty({ example: 321, description: 'Tweet reply count' })
  @Column('int')
  reply_count: number;

  @ApiProperty({ example: 420, description: 'Tweet like count' })
  @Column('int')
  like_count: number;

  @ApiProperty({ example: 111, description: 'Tweet quote count' })
  @Column('int')
  quote_count: number;
}
