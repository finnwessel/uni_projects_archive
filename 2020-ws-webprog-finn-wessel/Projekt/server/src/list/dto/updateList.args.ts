import { IsArray, IsBoolean, IsString } from 'class-validator';
import { ApiPropertyOptional } from '@nestjs/swagger';
import { Tweet } from '../../models/tweet.model';

export class UpdateListArgs {
  @IsString()
  @ApiPropertyOptional()
  name?: string;

  @IsBoolean()
  @ApiPropertyOptional()
  isPublic?: boolean;

  @IsArray()
  @ApiPropertyOptional()
  tweets?: Tweet[];
}
