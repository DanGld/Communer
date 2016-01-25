//
//  CommunityConversationsViewController.h
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CommunityConversationsViewController : UIViewController<UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) IBOutlet UITableView *table;
@property (strong, nonatomic) NSMutableArray *postsArray;
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
- (IBAction)backButtonClicked;
@end
