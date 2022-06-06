import { Column, Entity, OneToOne, PrimaryColumn } from 'typeorm';
import { AuthorMetrics } from './authorMetrics.model';
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class Author {
  @ApiProperty({
    example: '84364382748203478',
    description: 'The ID of the author',
  })
  @PrimaryColumn({ type: 'varchar', length: 50, unique: true })
  id: string;

  @ApiProperty({
    example: 'Max Mustermann',
    description: 'The name of the author',
  })
  @Column({ type: 'text', nullable: true })
  name: string;

  @ApiProperty({
    example: 'max_mustermann',
    description: 'The username of the author',
  })
  @Column({ type: 'text', nullable: true })
  username: string;

  @ApiProperty({ example: 'Berlin', description: 'The location of the author' })
  @Column({ type: 'text', nullable: true })
  location: string;

  @ApiProperty({
    example: '20 years old student',
    description: 'The description of the author',
  })
  @Column({ type: 'text', nullable: true })
  description: string;

  @ApiProperty({
    type: AuthorMetrics,
  })
  @OneToOne(() => AuthorMetrics, (authorMetrics) => authorMetrics.author, {
    cascade: true,
  })
  public_metrics: AuthorMetrics;

  @ApiProperty({
    example: true,
    description: 'The protected status of the author',
  })
  @Column('boolean')
  protected: boolean;

  @ApiProperty({
    example: 'https://example.url/profile-image.png',
    description: 'The profile image url of the author',
  })
  @Column({ type: 'text', nullable: true })
  profile_image_url: string;

  @ApiProperty({
    example: 'https://example.url',
    description: 'The url of the author',
  })
  @Column({ type: 'text', nullable: true })
  url: string;

  @ApiProperty({
    example: true,
    description: 'The verified status of the author',
  })
  @Column('boolean')
  verified: boolean;

  @ApiProperty({
    type: Date,
    example: 'Wed Dec 23 2020 15:13:54 GMT+0100',
    description: 'The created date of the author',
  })
  @Column('datetime')
  created_at: Date;
}
