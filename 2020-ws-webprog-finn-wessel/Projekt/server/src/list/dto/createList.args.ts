import { IsString, IsUUID } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateListArgs {
  @IsString()
  @ApiProperty()
  name: string;

  @IsString()
  @ApiProperty()
  sourceType: string;

  @IsString()
  @IsUUID()
  @ApiProperty()
  uuid: string;
}
