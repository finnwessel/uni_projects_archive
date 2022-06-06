import {
  Column,
  CreateDateColumn,
  Entity,
  JoinColumn,
  JoinTable,
  ManyToMany,
  ManyToOne,
  OneToMany,
  OneToOne,
  PrimaryColumn,
} from 'typeorm';
import { Author } from './author.model';
import { Url } from './url.model';
import { Media } from './media.model';
import { TweetMetrics } from './tweetMetrics.model';
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class Tweet {
  @ApiProperty({
    example: '83487382748203478',
    description: 'The ID of the tweet',
  })
  @PrimaryColumn({ type: 'varchar', length: 50, unique: true })
  id: string;

  @ApiProperty({ type: Author, description: 'The author of the tweet' })
  @ManyToOne(() => Author, {
    cascade: true,
  })
  @JoinColumn({ name: 'author', referencedColumnName: 'id' })
  author: Author; // Link to author Model

  @ApiProperty({
    example: 'This could be a tweet.',
    description: 'The text of the tweet',
  })
  @Column('text')
  text: string;

  @ApiProperty({ type: [Url], description: 'The tweet urls' })
  @OneToMany(() => Url, (url) => url.tweet, {
    cascade: true,
  })
  //@JoinColumn()
  urls: Url[]; // Link to url Model

  @ApiProperty({ type: [Media], description: 'The media of the tweet' })
  @ManyToMany(() => Media, {
    cascade: true,
  })
  @JoinTable()
  media: Media[]; // Link to Media Model

  @ApiProperty({ type: TweetMetrics, description: 'The public tweet metrics' })
  @OneToOne(() => TweetMetrics, (tweetMetrics) => tweetMetrics.tweet, {
    cascade: true,
  })
  public_metrics: TweetMetrics; // Link to Metrics Model

  @ApiProperty({
    example: false,
    description: 'Boolean if tweet might be sensitive',
  })
  @Column('boolean')
  possibly_sensitive: boolean;

  @ApiProperty({
    type: Date,
    example: 'Wed Dec 23 2020 15:13:54 GMT+0100',
    description: 'The created date of the tweet',
  })
  @Column('datetime')
  created_at: Date;

  @ApiProperty({
    type: Date,
    example: 'Wed Dec 23 2020 15:13:54 GMT+0100',
    description: 'The inserted date of the tweet',
  })
  @CreateDateColumn()
  inserted_at: Date;
}
