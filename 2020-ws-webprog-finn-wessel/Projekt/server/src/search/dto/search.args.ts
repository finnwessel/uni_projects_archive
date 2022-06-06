import {
  IsBoolean,
  IsBooleanString,
  IsNotEmpty,
  IsNumber,
  IsString,
  Max,
  Min,
} from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { Transform, Type } from 'class-transformer';

export class SearchArgs {
  @IsNotEmpty()
  @IsString()
  @ApiProperty()
  keywords: string;

  @IsBoolean()
  @ApiPropertyOptional()
  @Transform((it) => {
    switch (it) {
      case 'true':
        return true;
      case 'false':
        return false;
      default:
        return it;
    }
  })
  images?: boolean;

  @IsBoolean()
  @ApiPropertyOptional()
  @Transform((it) => {
    switch (it) {
      case 'true':
        return true;
      case 'false':
        return false;
      default:
        return it;
    }
  })
  videos?: boolean;

  @IsBoolean()
  @ApiPropertyOptional()
  @Transform((it) => {
    switch (it) {
      case 'true':
        return true;
      case 'false':
        return false;
      default:
        return it;
    }
  })
  retweets?: boolean;

  @Min(0)
  @IsNumber()
  @ApiPropertyOptional({ default: 0 })
  @Type(() => Number)
  skip = 0;

  @Min(10)
  @Max(100)
  @IsNumber()
  @ApiPropertyOptional({ default: 10 })
  @Type(() => Number)
  take?: number = 10;
}
