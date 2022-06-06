import {
  Controller,
  Get,
  Param,
  ParseUUIDPipe,
  Query,
  Req,
  UseGuards,
} from '@nestjs/common';
import { SearchArgs } from './dto/search.args';
import { SearchService } from './search.service';
import { Tweet } from '../models/tweet.model';
import { ApiAcceptedResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { JWTGuard } from '../auth/guards/jwt.guard';

@ApiTags('search')
@Controller('search')
export class SearchController {
  constructor(private readonly tweetsService: SearchService) {}

  @ApiOperation({ summary: 'Search recent streams with given keywords' })
  @ApiAcceptedResponse({ type: [Tweet] })
  @Get()
  @UseGuards(JWTGuard)
  async search(
    @Query() searchArgs: SearchArgs,
    @Req() request,
  ): Promise<Tweet[]> {
    return await this.tweetsService.search(searchArgs, request.user.id);
  }

  @ApiOperation({ summary: 'Get search with given id' })
  @Get('/:id')
  @UseGuards(JWTGuard)
  async getSearchWithId(@Param('id', new ParseUUIDPipe()) id: string) {
    return this.tweetsService.getSearchWithId(id);
  }

  @ApiOperation({ summary: 'Get settings of search with given id' })
  @Get('/:id/settings')
  @UseGuards(JWTGuard)
  async getSearchSettingsWithId(@Param('id', new ParseUUIDPipe()) id: string) {
    return this.tweetsService.getSearchSettingsWithId(id);
  }

  @ApiOperation({ summary: 'Get media content of tweet with given id' })
  @Get('tweet/:id/media')
  @UseGuards(JWTGuard)
  getVideoWithId(@Param('id') id: string) {
    return this.tweetsService.getVideoFromTweetWithId(id);
  }
}
