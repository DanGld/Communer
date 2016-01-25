//
//  SearchNewCommunityViewController.m
//  Smart Community
//
//  Created by oren shany on 9/1/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SearchNewCommunityViewController.h"
#import "CommunityPickerCell.h"
#import "Networking.h"
#import "DataManagement.h"
#import "UIImageView+AFNetworking.h"

@interface SearchNewCommunityViewController ()

@end

@implementation SearchNewCommunityViewController

UIView* blackLoadingLayer;
UIActivityIndicatorView* loader;
NSString* obj;
CommunitySearchModel* selectedCommunity;

- (void)viewDidLoad {
    [super viewDidLoad];
    _searchBar.delegate = self;
    _communitiesSearchTable.delegate = self;
    _communitiesSearchTable.dataSource = self;
    _connectAsGuestButton.enabled = NO;
    _beMemberButton.enabled = NO;
}

-(void)addObservers {
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(showSearchResponse)
                                                 name:@"reloadCommunitiesSearchTable"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(AddCommuitySuccess:)
                                                 name:@"addCommuitySuccess"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(AddCommuityFail)
                                                 name:@"addCommuityFail"
                                               object:obj];
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([appLanguage isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"מצא קהילה"];
        [_searchBar setPlaceholder:@"מצא לפי #האשטאג"];
        [_connectAsGuestButton setTitle:@"התחבר כאורח" forState:UIControlStateNormal];
        [_beMemberButton setTitle:@"הצטרף כחבר" forState:UIControlStateNormal];
    }
}

-(void)viewDidAppear:(BOOL)animated {
    [self addObservers];
    
   UIView* blackLayer = [[UIView alloc]initWithFrame:self.view.frame];
    [blackLayer setBackgroundColor:[UIColor blackColor]];
    [blackLayer setAlpha:0];
    blackLoadingLayer = blackLayer;
    [self.view addSubview:blackLoadingLayer];
    UIActivityIndicatorView* activityLoader = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    [activityLoader setHidesWhenStopped:YES];
    [activityLoader setCenter:self.view.center];
    loader = activityLoader;
    [self.view addSubview:loader];
}

-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self.view endEditing:YES];
    [self showLoader];
    [[Networking sharedInstance] getAllCommunitiesWithQuery:searchBar.text];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [DataManagement sharedInstance].communitySearchResults.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CommunityPickerCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CommunityPickerCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"CommunityPickerCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
   CommunitySearchModel* community = [DataManagement sharedInstance].communitySearchResults[indexPath.row];
    if ([community.imageURL class] != [NSNull class])
    {
    [cell.communityImage setImageWithURL:[NSURL URLWithString:community.imageURL] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    }
    [cell.communityName setText:community.name];
    [cell.communityLocation setText:[community.location.title class] != [NSNull class] ? community.location.title : @""];
    [cell.communityType setText:community.type];

    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [self.view endEditing:YES];
    CommunitySearchModel* community =  [DataManagement sharedInstance].communitySearchResults[indexPath.row];
    [_pickedCommunityImage.layer setCornerRadius:15.0f];
    [_pickedCommunityImage.layer setMasksToBounds:YES];
    if ([community.imageURL class] != [NSNull class]) {
    [_pickedCommunityImage setImageWithURL:[NSURL URLWithString:community.imageURL] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    }
    else {
        _pickedCommunityImage.image = nil;
    }
    
    [_pickedCommunityName setText:community.name];
    
    if ([community.location.title class] != [NSNull class]) {
    [_pickedCommunityLocation setText:community.location.title];
    }
    else {
        [_pickedCommunityLocation setText:@""];
    }
    
    if (community.roofOrg) {
    [_pickedCommunityGagOrgName setText:community.roofOrg];
    }
    else {
        [_pickedCommunityGagOrgName setText:@""];
    }
    
     [_pickedCommunityGagOrgType setText:community.type];
    selectedCommunity = community;
    
    _connectAsGuestButton.enabled = YES;
    _beMemberButton.enabled = YES;
    
}

-(void)showLoader {
    [UIView animateWithDuration:0.4 animations:^{
        [blackLoadingLayer setAlpha:0.5];
    }];
    [loader startAnimating];
}

-(void)hideLoader {
    [UIView animateWithDuration:0.4 animations:^{
        [blackLoadingLayer setAlpha:0];
    }];
    [loader stopAnimating];
}

-(void)showSearchResponse {
    [self hideLoader];
    [self.view endEditing:YES];
    [self.communitiesSearchTable reloadData];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}


- (IBAction)connectAsGuestClicked {
    [self showLoader];
    [[Networking sharedInstance] sendJoinRequestAsGuest:YES ForCommunityID:selectedCommunity.communityID];
}

- (IBAction)beMemberClicked {
    [self showLoader];
    [[Networking sharedInstance] sendJoinRequestAsGuest:NO ForCommunityID:selectedCommunity.communityID];
}

-(void)AddCommuitySuccess:(NSNotification*)notification {
    
    [self hideLoader];
    CommunityModel* communityObj = notification.object;
    [DataManagement sharedInstance].currentCommunityID = communityObj.communityID;
    [[NSUserDefaults standardUserDefaults] setObject:communityObj.communityID forKey:@"current_communityID"];
    [[NSUserDefaults standardUserDefaults] synchronize];
    NSArray* communitiesArr = [DataManagement sharedInstance].user.communities;
    BOOL contains = NO;
    for (CommunityModel* community in communitiesArr) {
        if ([community.communityID isEqualToString:communityObj.communityID]) {
            contains = YES;
        }
    }
    if (!contains) {
        [[DataManagement sharedInstance].user.communities addObject:communityObj];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                        object:nil
                                                      userInfo:nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"updateMenuDetails"
                                                        object:nil
                                                      userInfo:nil];
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void)AddCommuityFail {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Add community failed" message:@"Oops.. something went wrong, try again" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}

- (IBAction)backButtonClicked {
    if (!_disableBack) {
    [self dismissViewControllerAnimated:YES completion:nil];
    }
    else {
        UIAlertView* alert =[[UIAlertView alloc]initWithTitle:@"No Communities" message:@"You have to connect to at least 1 community for using the app, try to search for your community" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    }
}

@end
