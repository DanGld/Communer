//
//  SideMenuViewController.h
//  Smart Community
//
//  Created by Jukk on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MFSideMenu.h"

@interface SideMenuViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITableView *menuTableView;
@property (strong, nonatomic) IBOutlet UIImageView *communityBGImage;
@property (strong, nonatomic) IBOutlet UILabel *guestLabel;
@property (strong, nonatomic) IBOutlet UIImageView *roofOrgImage;
@property (strong, nonatomic) IBOutlet UILabel *roofOrgName;
@property (strong, nonatomic) IBOutlet UILabel *communityName;
@property (strong, nonatomic) IBOutlet UILabel *communityLocation;
@property (strong, nonatomic) IBOutlet UITableView *communitiesTable;

@end
