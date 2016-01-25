//
//  CommentCell.m
//  Smart Community
//
//  Created by Eyal Makover on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CommentCell.h"

@implementation CommentCell

- (void)awakeFromNib {
    [_commentImage.layer setMasksToBounds:YES];
    [_commentImage.layer setCornerRadius:23];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)setStars:(int)rate {
    switch (rate) {
        case 1:
            [_star1 setImage:[UIImage imageNamed:@"gray_staer"]];
            break;
        case 2:
            [_star1 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star2 setImage:[UIImage imageNamed:@"gray_staer"]];
            break;
        case 3:
            [_star1 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star2 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star3 setImage:[UIImage imageNamed:@"gray_staer"]];
            break;
        case 4:
            [_star1 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star2 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star3 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star4 setImage:[UIImage imageNamed:@"gray_staer"]];
            break;
        case 5:
            [_star1 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star2 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star3 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star4 setImage:[UIImage imageNamed:@"gray_staer"]];
            [_star5 setImage:[UIImage imageNamed:@"gray_staer"]];
            break;
            
        default:
            break;
    }
}

@end
