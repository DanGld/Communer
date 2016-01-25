//
//  HomeViewController.h
//  Smart Community
//
//  Created by oren shany on 7/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LogicServices.h"
#import "nextServicesCell.h"
#import "messageCell.h"
#import "QuestionCell.h"
#import "CommunityPostCell.h"
#import "MFSideMenu.h"
#import <Pusher/Pusher.h>

@interface HomeViewController : UIViewController<UITableViewDelegate,UITableViewDataSource>


@property (strong, nonatomic) IBOutlet UILabel *tabTitle;
@property (strong, nonatomic) IBOutlet UIImageView *communityImage;
@property (strong, nonatomic) IBOutlet UITableView *table;
@property (strong, nonatomic) IBOutlet UILabel *communityName;
@property (strong, nonatomic) IBOutlet UILabel *communityLocation;
@property(nonatomic)MFSideMenuContainerViewController *container;
@property (strong) PTPusher* client;


- (IBAction)menuAction:(id)sender;
- (void)EventCellClicked;

@end
