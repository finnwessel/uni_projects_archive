import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Tweet } from './tweet.model';
import { Exclude } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class Url {
  @Exclude()
  @PrimaryGeneratedColumn('uuid')
  uid: string;

  @ApiProperty({ example: '12', description: 'Url start' })
  @Column({ type: 'varchar', nullable: true })
  start: string;

  @ApiProperty({ example: '28', description: 'Url end' })
  @Column({ type: 'varchar', nullable: true })
  end: string;

  @ApiProperty({ example: 'https://t.co/8937216', description: 'Url' })
  @Column({ type: 'varchar', nullable: true })
  url: string;

  @ApiProperty({
    example: 'https://example.url/example/903284982983402',
    description: 'Url start',
  })
  @Column({ type: 'varchar', nullable: true })
  expanded_url: string;

  @ApiProperty({
    example: 'https://example.url/example',
    description: 'Display url',
  })
  @Column({ type: 'varchar', nullable: true })
  display_url: string;

  @ManyToOne(() => Tweet, (tweet) => tweet.urls)
  tweet: Tweet;
}
