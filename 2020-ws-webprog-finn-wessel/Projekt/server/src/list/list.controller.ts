import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseUUIDPipe,
  Patch,
  Post,
  Req,
  UseGuards,
} from '@nestjs/common';
import { JWTGuard } from '../auth/guards/jwt.guard';
import { ListService } from './list.service';
import { UpdateListArgs } from './dto/updateList.args';
import { CreateListArgs } from './dto/createList.args';
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger';

@ApiTags('list')
@Controller('list')
export class ListController {
  constructor(private readonly listService: ListService) {}

  @ApiOperation({ summary: 'Get all lists of current user' })
  @Get()
  @UseGuards(JWTGuard)
  getAllListsFromUser(@Req() request) {
    const user = request.user;
    return this.listService.getAllListsFromUser(user.id);
  }

  @ApiOperation({ summary: 'Get list with given id' })
  @Get('/:listId')
  @UseGuards(JWTGuard)
  getListWithId(
    @Req() request,
    @Param('listId', new ParseUUIDPipe()) listId: string,
  ) {
    const user = request.user;
    return this.listService.getListWithId(user.id, listId);
  }

  @ApiOperation({ summary: 'Create list' })
  @Post()
  @UseGuards(JWTGuard)
  createList(@Req() request, @Body() createListArgs: CreateListArgs) {
    const user = request.user;
    return this.listService.createList(user.id, createListArgs);
  }

  @ApiOperation({ summary: 'Update list' })
  @Patch('/:listId')
  @UseGuards(JWTGuard)
  updateList(
    @Req() request,
    @Param('listId', new ParseUUIDPipe()) listId: string,
    @Body() updateListArgs: UpdateListArgs,
  ) {
    const user = request.user;
    return this.listService.updateListWithId(user.id, listId, updateListArgs);
  }

  @Delete('/:listId')
  @UseGuards(JWTGuard)
  deleteList(
    @Req() request,
    @Param('listId', new ParseUUIDPipe()) listId: string,
  ) {
    const user = request.user;
    return this.listService.deleteListWithId(user.id, listId);
  }
}
