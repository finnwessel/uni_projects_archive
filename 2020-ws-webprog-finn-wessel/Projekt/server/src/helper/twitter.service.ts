import { HttpException, HttpService, Injectable } from '@nestjs/common';
import { catchError, map } from 'rxjs/operators';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class TwitterService {
  constructor(
    private readonly httpService: HttpService,
    private readonly configService: ConfigService,
  ) {}

  async getRules(): Promise<any> {
    return await this.getFromTwitterApi(
      'https://api.twitter.com/2/tweets/search/stream/rules',
    );
  }

  async addRule(keywords: string): Promise<string> {
    const reqObject = {
      add: [{ value: keywords, tag: keywords }],
    };
    return await this.postToTwitterApi(
      'https://api.twitter.com/2/tweets/search/stream/rules',
      reqObject,
    )
      .then((r) => {
        return r.data.find((rule) => rule.value === keywords).id;
      })
      .catch((error) => console.debug(error));
  }

  async deleteRules(rules: Array<string>): Promise<boolean> {
    const reqObject = {
      delete: {
        ids: rules,
      },
    };
    return await this.postToTwitterApi(
      'https://api.twitter.com/2/tweets/search/stream/rules',
      reqObject,
    )
      .then((r) => {
        return (
          r &&
          r.meta &&
          r.meta.summary &&
          r.meta.summary.deleted &&
          r.meta.summary.deleted === rules.length
        );
      })
      .catch((error) => {
        console.log(error);
        return false;
      });
  }

  async deleteAllRules() {
    this.getRules().then(async (r) => {
      console.log('Get current rules');
      if (r.data) {
        const rules = r.data.map((rule) => rule.id) || [];
        if (rules.length > 0) {
          this.deleteRules(rules)
            .then(async () => {
              console.log('Deleted all rules');
            })
            .catch((e) => console.warn(e));
        }
      } else {
        console.log('No rules to delete');
      }
    });
  }

  async getStream(): Promise<any> {
    const url =
      'https://api.twitter.com/2/tweets/search/stream?tweet.fields=attachments,author_id,context_annotations,conversation_id,created_at,entities,geo,id,in_reply_to_user_id,lang,possibly_sensitive,public_metrics,referenced_tweets,reply_settings,source,text,withheld&expansions=attachments.poll_ids,attachments.media_keys,author_id,entities.mentions.username,geo.place_id,in_reply_to_user_id,referenced_tweets.id,referenced_tweets.id.author_id&user.fields=created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld&media.fields=duration_ms,height,media_key,preview_image_url,type,url,width,public_metrics&place.fields=contained_within,country,country_code,full_name,geo,id,name,place_type&poll.fields=duration_minutes,end_datetime,id,options,voting_status';
    const token = this.configService.get<string>('TWITTER_API_KEY');
    const headers = {
      Authorization: `Bearer ${token}`,
    };
    return await this.httpService
      .get(url, { headers, responseType: 'stream' })
      .toPromise()
      .then((response) => response.data)
      .catch((error) => console.log(error));
  }

  // Api requests

  async getFromTwitterApi(url): Promise<any> {
    const token = this.configService.get<string>('TWITTER_API_KEY');

    const headersRequest = {
      Authorization: `Bearer ${token}`,
    };
    return await this.httpService
      .get(url, { headers: headersRequest })
      .pipe(
        map((response) => response.data),
        catchError((e) => {
          throw new HttpException(e.response.data, e.response.status);
        }),
      )
      .toPromise()
      .catch((e) => {
        throw new HttpException(e.message, e.code);
      });
  }

  async postToTwitterApi(url, data): Promise<any> {
    const token = this.configService.get<string>('TWITTER_API_KEY');

    const headersRequest = {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };
    return this.httpService
      .post(url, data, { headers: headersRequest })
      .pipe(
        map((response) => {
          return response.data;
        }),
      )
      .toPromise()
      .catch((error) => {
        console.log(error);
      });
  }
}
