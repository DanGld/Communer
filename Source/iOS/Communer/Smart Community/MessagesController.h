//
//  MessagesController.h
//  Smart Community
//
//  Created by oren shany on 8/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EventCell.h"
#import "messageCell.h"
#import "LogicServices.h"
#import "MFSideMenu.h"
#import "SingleEventViewController.h"
#import "SingleActivitieViewController.h"

@interface MessagesController : UIViewController<UITableViewDataSource,UITableViewDelegate>
@property (strong, nonatomic) IBOutlet UILabel *tabTitle;
@property (strong, nonatomic) IBOutlet UITableView *table;
@property (weak, nonatomic) IBOutlet UIView *blackCoverView;
@property (weak, nonatomic) IBOutlet UIView *messagePopupView;
@property (weak, nonatomic) IBOutlet UITextView *popupTextView;
@property (weak, nonatomic) IBOutlet UILabel *popupTitle;
@property (weak, nonatomic) IBOutlet UIImageView *popupImage;
@property (weak, nonatomic) IBOutlet UILabel *popupDate;
@property (strong, nonatomic) IBOutlet UIImageView *communityImage;
@property (strong, nonatomic) IBOutlet UILabel *communityName;
@property (strong, nonatomic) IBOutlet UILabel *communityLocation;
@property(nonatomic)MFSideMenuContainerViewController *container;
- (IBAction)menuAction:(id)sender;

@end
