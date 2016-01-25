//
//  SecurityCell.h
//  Smart Community
//
//  Created by Eyal Makover on 9/24/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SecurityEventModel.h"

@interface SecurityCell : UITableViewCell
@property (strong, nonatomic) IBOutlet UILabel *reportType;
@property (strong, nonatomic) IBOutlet UILabel *reporterName;
@property (strong, nonatomic) IBOutlet UILabel *reportLocation;
@property (strong, nonatomic) IBOutlet UIImageView *reportSeverity;
@property (strong, nonatomic) IBOutlet UIImageView *securityIcon;
@property (strong, nonatomic) IBOutlet UILabel *hasbeenReprotedLabel;
@property (strong, nonatomic) IBOutlet UILabel *readMoreLabel;

- (void)initWithReport:(SecurityEventModel*)event;

@end
