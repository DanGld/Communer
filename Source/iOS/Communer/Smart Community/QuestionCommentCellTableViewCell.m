//
//  QuestionCommentCellTableViewCell.m
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "QuestionCommentCellTableViewCell.h"
#import "UIImageView+AFNetworking.h"

@implementation QuestionCommentCellTableViewCell

- (void)awakeFromNib {
    [_commentImage.layer setCornerRadius:20];
    [_commentImage.layer setMasksToBounds:YES];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)initWithComment:(CommentModel *)comment
{
    if (![comment.name isKindOfClass:[NSNull class]]) {
        _commentName.text = comment.name;
    }
    
    NSURL *url = [NSURL URLWithString:comment.imageUrl];
    [_commentImage setImageWithURL:url placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
    NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
    [dateFormatter setTimeZone:tz];
    NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:comment.date/1000]];
;
    _commentDate.text = formattedDateString;
    _commentContent.text = comment.content;
    if (!comment.isRebai) {
        [_commentContent setTextColor:[UIColor blackColor]];
    }
}

@end
