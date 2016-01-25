//
//  ServiceContactCell.m
//  Smart Community
//
//  Created by oren shany on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "FavoriteServiceContactCell.h"


@implementation FavoriteServiceContactCell

- (void)awakeFromNib {
    [_contactImage.layer setCornerRadius:25];
    [_contactImage.layer setMasksToBounds:YES];
}

-(void)setStars {
    if ([_contactRating.text doubleValue]>1)
        [_star1 setImage:[UIImage imageNamed:@"white_star"]];
    
    if ([_contactRating.text doubleValue]>2)
        [_star2 setImage:[UIImage imageNamed:@"white_star"]];
    
    if ([_contactRating.text doubleValue]>3)
        [_star3 setImage:[UIImage imageNamed:@"white_star"]];
    
    if ([_contactRating.text doubleValue]>4)
        [_star4 setImage:[UIImage imageNamed:@"white_star"]];
    
    if ([_contactRating.text doubleValue]==5)
        [_star5 setImage:[UIImage imageNamed:@"white_star"]];
}

@end
