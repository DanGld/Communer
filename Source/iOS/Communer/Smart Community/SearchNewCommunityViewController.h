//
//  SearchNewCommunityViewController.h
//  Smart Community
//
//  Created by oren shany on 9/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SearchNewCommunityViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UISearchBarDelegate>
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UISearchBar *searchBar;
@property (strong, nonatomic) IBOutlet UITableView *communitiesSearchTable;
@property (strong, nonatomic) IBOutlet UIImageView *pickedCommunityImage;
@property (strong, nonatomic) IBOutlet UILabel *pickedCommunityName;
@property (strong, nonatomic) IBOutlet UILabel *pickedCommunityLocation;
@property (strong, nonatomic) IBOutlet UILabel *pickedCommunityGagOrgName;
@property (strong, nonatomic) IBOutlet UILabel *pickedCommunityGagOrgType;
@property (strong, nonatomic) IBOutlet UIButton *connectAsGuestButton;
@property (strong, nonatomic) IBOutlet UIButton *beMemberButton;
@property (strong, nonatomic) IBOutlet UIButton *backButton;
@property (nonatomic) BOOL disableBack;

-(void)showSearchResponse;
- (IBAction)connectAsGuestClicked;
- (IBAction)beMemberClicked;
@end
