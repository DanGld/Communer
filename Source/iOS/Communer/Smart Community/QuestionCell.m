//
//  QuestionCell.m
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "QuestionCell.h"
#import "UIImageView+AFNetworking.h"

@implementation QuestionCell

- (void)awakeFromNib {
    [_image.layer setMasksToBounds:YES];
    [_image.layer setCornerRadius:18];
}

-(void)initWithQuestion:(postModel*)question {
    [_title setText:question.title];
    if ([question.communityMember isKindOfClass:[CommunityMemberModel class]]) {
    [_name setText:question.communityMember.name];
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:question.communityMember.imageUrl]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        
        [_image setImageWithURLRequest:imageRequest
                      placeholderImage:[UIImage imageNamed:@"noprofilepic.jpg"]
                               success:nil
                               failure:nil];
    }
    
    [_content setText:question.content];
    if (question.comments.count>1) {
        NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
        if ([language isEqualToString:@"english"]) {
    [_numOfComments setText:[NSString stringWithFormat:@"%lu Comments" , (unsigned long)question.comments.count]];
        }
        if ([language isEqualToString:@"hebrew"]) {
            [_numOfComments setText:[NSString stringWithFormat:@"%lu תגובות" , (unsigned long)question.comments.count]];
        }
    }
    else {
        if (question.comments.count==1) {
            NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
            if ([language isEqualToString:@"english"]) {
             [_numOfComments setText:@"1 Comment"];
            }
            if ([language isEqualToString:@"hebrew"]) {
                [_numOfComments setText:@"1 תגובה"];
            }
        }
        else {
            [_numOfComments setHidden:YES];
        }
    }
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
    [dateFormatter setTimeZone:tz];
    NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:question.cDate/1000]];
    if (![formattedDateString containsString:@"1970"]) {
    [_date setText:formattedDateString];
    }
}

@end
