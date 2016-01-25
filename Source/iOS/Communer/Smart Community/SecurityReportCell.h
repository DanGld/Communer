//
//  SecurityReportCell.h
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SecurityReportCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UIButton *leftCircle;
@property (strong, nonatomic) IBOutlet UIButton *rightCircle;
@property (strong, nonatomic) IBOutlet UILabel *viewReportsLabel;
@property (strong, nonatomic) IBOutlet UILabel *createReportLabel;

- (IBAction)ViewReportsClicked;
- (IBAction)createReportClicked;

@end
