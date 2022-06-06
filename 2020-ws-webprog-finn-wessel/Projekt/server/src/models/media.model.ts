import { Column, Entity, PrimaryColumn } from 'typeorm';
import { ApiProperty } from '@nestjs/swagger';

@Entity()
export class Media {
  @PrimaryColumn({ type: 'varchar', length: 50, unique: true })
  media_key: string;

  @ApiProperty({ example: 'video', description: 'Media type' })
  @Column('varchar')
  type: string;

  @ApiProperty({
    example: 'https://example.url/video.mp4',
    description: 'Media url',
  })
  @Column({ type: 'varchar', nullable: true })
  url: string;

  @ApiProperty({ example: 500, description: 'Media width' })
  @Column({ type: 'int', nullable: true })
  width: number;

  @ApiProperty({ example: 500, description: 'Media height' })
  @Column({ type: 'int', nullable: true })
  height: number;
  // Video specific fields
  @ApiProperty({ example: 3434, description: 'Media duration' })
  @Column({ type: 'int', nullable: true })
  duration_ms: number;

  @ApiProperty({
    example: 'https://example.url/picture.png',
    description: 'Media preview url',
  })
  @Column({ type: 'varchar', nullable: true })
  preview_image_url: string;
}
