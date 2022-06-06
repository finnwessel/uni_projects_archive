import {
  Injectable,
  NotImplementedException,
  OnModuleInit,
  Logger,
} from '@nestjs/common';
import { Subject } from 'rxjs';
import { MessageEvent } from './dto/messageEvent.interface';

import { TwitterService } from '../helper/twitter.service';
import { sortKeyWordsAlphabetical } from '../helper/stream';
import parseStreamTweets from 'src/helper/parseStreamTweet';
import { StreamRepository } from './stream.repository';
@Injectable()
export class StreamService implements OnModuleInit {
  constructor(
    private readonly twitterService: TwitterService,
    private readonly streamRepository: StreamRepository,
  ) {}

  private readonly logger = new Logger(StreamService.name);

  // function called on server start
  onModuleInit() {
    this.logger.log('Resetting stream rules');
    // delete all rules
    this.twitterService.deleteAllRules().catch(() => {
      this.logger.warn('Failed to delete old rules');
    });
  }

  private twitter_stream = null;
  private twitter_stream_close_timeout = null;
  private rules = [];

  async handleStream(keywords, req): Promise<Subject<MessageEvent>> {
    keywords = sortKeyWordsAlphabetical(keywords);
    // Create Id for user
    const clientId = Date.now();
    // check if any stream has the desired keywords
    let rule = this.rules.find((obj) => {
      return obj.keywords === keywords;
    });

    // if the rule exists
    if (rule && rule.users) {
      // add the user to the array
      rule.users.push({
        id: clientId,
      });
    } else {
      // try tro create rule
      // check if there are no more than 24 rules
      if (this.rules.length < 25) {
        const savedStream = await this.streamRepository.createAndSaveStream(
          keywords,
        );
        // try to add the rule and get the id
        const id = await this.twitterService.addRule(keywords);
        // if rule added successfully
        if (id) {
          rule = {
            id: id,
            dbId: savedStream.id,
            subject: new Subject<any>(),
            keywords: keywords,
            users: [
              {
                id: clientId,
              },
            ],
          };
          this.rules.push(rule);
          this.logger.verbose(
            `Added rule ${rule.id} with keywords: ${rule.keywords}`,
          );
          // otherwise throw exception
        } else {
          throw new NotImplementedException('Failed to add the rule');
        }
      } else {
        throw new NotImplementedException('Already 25 Rules in use');
      }
    }

    // Now rule should be set up, try to start the stream
    this.openTwitterStream().catch((error) => {
      this.logger.error('Error opening twitter stream', error);
    });

    // handle user disconnect
    req.on('end', () => {
      this.logger.verbose(`${clientId} Connection closed`);
      if (rule && rule.users) {
        // remove user from rules users
        rule.users = rule.users.filter(
          (c: { id: number }) => c.id !== clientId,
        );
        // if no user is connected to the rule, remove the rule
        if (rule.users.length == 0) {
          // call remove rule function from twitter
          this.twitterService.deleteRules([rule.id]).then((deleted) => {
            if (deleted) {
              // filter the rule from rules array
              this.rules = this.rules.filter(
                (c: { id: number }) => c.id !== rule.id,
              );
              this.logger.verbose(
                `Deleted stream rule ${rule.id} with keywords: ${rule.keywords}`,
              );
              if (this.rules.length < 1) {
                this.closeTwitterStreamAfterTimeout();
              }
            } else {
              this.logger.warn('Failed to delete stream rule.');
            }
          });
        }
      }
    });
    setTimeout(() => {
      rule.subject.next({
        id: 0,
        type: 'info',
        data: {
          stream_id: rule.dbId,
        },
      });
    }, 20);
    return rule.subject;
  }

  private async openTwitterStream() {
    this.twitter_stream_close_timeout = null;
    if (this.twitter_stream === null) {
      this.twitter_stream = await this.twitterService.getStream();
      this.twitter_stream.on('data', (buffer) => {
        const bufferString = buffer.toString();
        // twitter sends /n every x seconds which must be filtered
        if (bufferString.length > 2) {
          // ToDO: add try and catch for JSON.parse()
          try {
            const parsedTweet = JSON.parse(bufferString);
            parsedTweet.matching_rules.forEach((rule) => {
              const foundRule = this.rules.find((r) => {
                return r.id == rule.id;
              });
              if (foundRule) {
                const tweet = parseStreamTweets(parsedTweet);
                foundRule.subject.next({
                  type: 'tweet',
                  data: tweet,
                  id: tweet.id,
                });
                this.streamRepository.saveTweetWithStreamId(
                  tweet,
                  foundRule.dbId,
                );
              }
            });
          } catch (e) {
            // Do nothing if response is not parsable
          }
        }
      });
      this.twitter_stream.on('error', (error) => {
        this.logger.error('Error on twitter stream connection', error);
      });
    }
  }

  private closeTwitterStreamAfterTimeout() {
    // Close stream after 15 min inactivity
    this.twitter_stream_close_timeout = setTimeout(() => {
      if (this.twitter_stream !== null) {
        this.twitter_stream.destroy();
        this.twitter_stream = null;
        this.logger.verbose('Closing stream due to inactivity');
      }
    }, 900000);
  }

  async getStreamSettingsWithId(id: string) {
    return this.streamRepository.getStreamSettingsWithId(id);
  }
}
