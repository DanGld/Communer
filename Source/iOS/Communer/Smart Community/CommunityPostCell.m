//
//  CommunityPostCell.m
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CommunityPostCell.h"
#import "UIImageView+AFNetworking.h"

@implementation CommunityPostCell

- (void)awakeFromNib {
    [_image.layer setMasksToBounds:YES];
    [_image.layer setCornerRadius:18];
}

- (void)initWithPost:(postModel *)post
{
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];

    _title.text = post.title;
    NSURL *url = [NSURL URLWithString:post.imageUrl];
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:url
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [_image setImageWithURLRequest:imageRequest
                             placeholderImage:[UIImage imageNamed:@"noprofilepic.jpg"]
                                      success:nil
                                      failure:nil];

    if (![post.communityMember isKindOfClass:[NSNull class]] && ![post.communityMember.name isKindOfClass:[NSNull class]]) {
        _userName.text = post.communityMember.name;
    }
    _text.text = post.content;
    
    if (post.comments.count>1) {
        
        if ([appLanguage isEqualToString:@"english"]) {
        [_commentsNumber setText:[NSString stringWithFormat:@"%lu Comments" , (unsigned long)post.comments.count]];
        }
        if ([appLanguage isEqualToString:@"hebrew"]) {
            [_commentsNumber setText:[NSString stringWithFormat:@"תגובות %lu " , (unsigned long)post.comments.count]];
        }

    }
    else {
        if (post.comments.count==1) {
            if ([appLanguage isEqualToString:@"hebrew"]) {
            [_commentsNumber setText:@"1 Comment"];
            }
            if ([appLanguage isEqualToString:@"hebrew"]) {
                [_commentsNumber setText:@"תגובה 1"];
            }
        }
        else {
            [_commentsNumber setHidden:YES];
        }
    }

    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
    [dateFormatter setTimeZone:tz];
    NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:post.cDate/1000]];
     if (![formattedDateString containsString:@"1970"]) {
    _date.text = formattedDateString;
     }
}

@end
