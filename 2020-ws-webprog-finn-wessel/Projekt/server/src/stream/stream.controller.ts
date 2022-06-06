import {
  Controller,
  Get,
  Param,
  ParseUUIDPipe,
  Query,
  Req,
  Sse,
  UseGuards,
} from '@nestjs/common';
import { Subject } from 'rxjs';
import { MessageEvent } from './dto/messageEvent.interface';
import { StreamService } from './stream.service';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import { JWTGuard } from '../auth/guards/jwt.guard';

@ApiTags('stream')
@Controller('stream')
export class StreamController {
  constructor(private readonly streamService: StreamService) {}

  @ApiOperation({ summary: 'Connect to stream with given keywords' })
  @Sse()
  async getActiveStream(
    @Query('keywords') keywords: string,
    @Req() req,
  ): Promise<Subject<MessageEvent>> {
    return await this.streamService.handleStream(keywords, req);
  }

  @ApiOperation({ summary: 'Get settings of stream with given id' })
  @Get('/:id/settings')
  @UseGuards(JWTGuard)
  async getStreamSettingsWithId(@Param('id', new ParseUUIDPipe()) id: string) {
    return this.streamService.getStreamSettingsWithId(id);
  }
}
