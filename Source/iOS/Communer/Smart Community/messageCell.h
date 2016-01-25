//
//  messageCell.h
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface messageCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UILabel *messageTitle;
@property (strong, nonatomic) IBOutlet UILabel *cDate;
@property (strong, nonatomic) IBOutlet UILabel *messageContent;
@property (strong, nonatomic) IBOutlet UIImageView *messageImage;
- (IBAction)readMoreButton;

@end
