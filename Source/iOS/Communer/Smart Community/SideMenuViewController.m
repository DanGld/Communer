//
//  SideMenuViewController.m
//  Smart Community
//
//  Created by Jukk on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SideMenuViewController.h"
#import "MenuCell.h"
#import "QuestionsAndAnswersViewController.h"
#import "FacilitiesViewController.h"
#import "UIImageView+AFNetworking.h"
#import "LogicServices.h"
#import "CommunityCell.h"
#import "FindCommunityCell.h"
#import "UIImageView+AFNetworking.h"
#import "SearchNewCommunityViewController.h"
#import "CommunityConversationsViewController.h"
#import "myProfileViewController.h"
#import "SecurityViewController.h"
#import "CommunityProfileViewController.h"
#import "SupportViewController.h"

@interface SideMenuViewController ()

@end

@implementation SideMenuViewController
{
    NSMutableArray *imageArray;
    NSMutableArray *textArray;
    NSMutableArray *imageArray2;
    NSMutableArray *textArray2;
}

NSArray* communitiesArr;
NSString* obji;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.communitiesTable.delegate = self ;
    self.communitiesTable.dataSource = self;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(updateMenuView)
                                                 name:@"updateMenuDetails"
                                               object:obji];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(closeCommunitiesList)
                                                 name:@"closeCommunitiesList"
                                               object:obji];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appLanguageChange)
                                                 name:@"appLanguageChange"
                                               object:obji];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(updateMenuWithClick)
                                                 name:@"updateMenuWithClick"
                                               object:obji];
    [self updateMenuView];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)closeCommunitiesList {
    [_communitiesTable setAlpha:0];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (tableView!=_communitiesTable) { // menu
    if (section == 0) {
        return textArray.count;
    } else {
        return textArray2.count;
    }
    }
    else {  // communities list
        if (section == 0)
        {
            communitiesArr =  [DataManagement sharedInstance].user.communities;
            return communitiesArr.count;
        }
        
        if (section == 1)
            return 1;
    }
    return 0;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView!=_communitiesTable) {
    return 60;
    }
    else {
        return 55;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if (tableView!=_communitiesTable) {
    if (section == 0) {
        return 30;
    }
    }
    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView != _communitiesTable)  {
    MenuCell *cell; 
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"MenuCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    if (indexPath.section == 0) {
        [cell initWithImageAndText:[UIImage imageNamed:[imageArray objectAtIndex:indexPath.row]] andText:[textArray objectAtIndex:indexPath.row]];
    } else {
        [cell initWithImageAndText:[UIImage imageNamed:[imageArray2 objectAtIndex:indexPath.row]] andText:[textArray2 objectAtIndex:indexPath.row]];
    }
    return cell;
    }
    else {
        if (indexPath.section == 0) {
            CommunityCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CommunityCell"];
            if (cell == nil) {
                // Load the top-level objects from the custom cell XIB.
                NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"CommunityCell" owner:self options:nil];
                // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                cell = [topLevelObjects objectAtIndex:0];
            }
            CommunityModel* community = communitiesArr[indexPath.row];
            [cell.communityImage setImageWithURL:[NSURL URLWithString:community.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
            [cell.communityName setText:community.communityName];
            [cell.communityLocation setText:community.locationObj.title];
            return cell;
        }
        else {
                NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"FindCommunityCell" owner:self options:nil];
                // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                FindCommunityCell* findCell = [topLevelObjects objectAtIndex:0];
            return findCell;
            }
        }

}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView != _communitiesTable) {
    if (indexPath.section == 0) {
        if ([textArray[indexPath.row] isEqualToString:@"Home"] || [textArray[indexPath.row] isEqualToString:@"בית"]) {
            UITabBarController *controller = self.menuContainerViewController.centerViewController;
            [controller setSelectedIndex:0];
        }
        if ([textArray[indexPath.row] isEqualToString:@"Security"] || [textArray[indexPath.row] isEqualToString:@"ביטחון"]) {
            SecurityViewController * conversations  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"SecurityViewController"];
            [self presentViewController:conversations animated:YES completion:nil];
        }
        if ([textArray[indexPath.row] isEqualToString:@"Q&A"] || [textArray[indexPath.row] isEqualToString:@"שו״ת"]) {
            QuestionsAndAnswersViewController *questions = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"QuestionsAndAnswersViewController"];
            [self presentViewController:questions animated:YES completion:nil];
        }
        if ([textArray[indexPath.row] isEqualToString:@"Discussions"] || [textArray[indexPath.row] isEqualToString:@"שיח קהילתי"]) {
            CommunityConversationsViewController * conversations  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"CommunityConversationsViewController"];
            [self presentViewController:conversations animated:YES completion:nil];
        }
        if ([textArray[indexPath.row] isEqualToString:@"Facilities"] || [textArray[indexPath.row] isEqualToString:@"מתקנים"]) {
            FacilitiesViewController* facility = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"FacilitiesViewController"];
            [self presentViewController:facility animated:YES completion:nil];
        }
        if ([textArray[indexPath.row] isEqualToString:@"Prayer Times"] || [textArray[indexPath.row] isEqualToString:@"זמני תפילות"]) {
            FacilitiesViewController* prayerTimes = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"PrayersTimeController"];
            [self presentViewController:prayerTimes animated:YES completion:nil];
        }
    }
    else {
        switch (indexPath.row) {
            case 0: // My Profile
            {
                myProfileViewController* myProfile  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"updateDetails"];
                myProfile.isUpdateDetails = YES;
                [self presentViewController:myProfile animated:YES completion:nil];
                 break;
            }
               
            case 1: // Community Profile
            {
                CommunityProfileViewController* communityProfile  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"CommunityProfileViewController"];
                [self presentViewController:communityProfile animated:YES completion:nil];
                break;
            }
                
            case 2: // Support
            {
                SupportViewController* supportController  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"SupportViewController"];
                [self presentViewController:supportController animated:YES completion:nil];
                break;
            }
            default:
                break;
        }
    }
    
    
    [self.menuContainerViewController setMenuState:MFSideMenuStateClosed completion:^{}];
    }
    else {
        if (indexPath.section ==0) { // change current communityID
            CommunityModel* community = communitiesArr[indexPath.row];
            [DataManagement sharedInstance].lastCurrentCommunityID = [[DataManagement sharedInstance] currentCommunityID];
            [DataManagement sharedInstance].currentCommunityID = community.communityID;
            [[NSUserDefaults standardUserDefaults] setObject:community.communityID forKey:@"current_communityID"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
             [self updateMenuView];
            
              [self.menuContainerViewController setMenuState:MFSideMenuStateClosed completion:^{}];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData"
                                                                object:nil
                                                              userInfo:nil];
        }
        else { // find community
            SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];

            [self presentViewController:controller animated:YES completion:nil];
        }
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

-(void)updateMenuView {
    CommunityModel* currentCommunity = [[LogicServices sharedInstance] getCurrentCommunity];
    
    if (currentCommunity) {
        [_communityBGImage setImageWithURL:[NSURL URLWithString:currentCommunity.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
        [_communityName setText:currentCommunity.communityName];
        [_communityLocation setText:currentCommunity.locationObj.title];
        if (currentCommunity.roofOrg && currentCommunity.roofOrg.communityName) {
            [_roofOrgImage setImageWithURL:[NSURL URLWithString:currentCommunity.roofOrg.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
            [_roofOrgName setText:currentCommunity.roofOrg.communityName];
        }
        else {
            [_roofOrgImage setHidden:YES];
            [_roofOrgName setHidden:YES];
        }
        
        if ([currentCommunity.memberType isEqualToString:@"guest"]) {
            [_guestLabel setHidden:NO];
        }
        else {
            [_guestLabel setHidden:YES];
        }
        
        NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
        imageArray = [NSMutableArray arrayWithObjects:@"home", nil];
        
        if ([appLanguage isEqualToString:@"english"]) {
            [_guestLabel setText:@"GUEST"];
        textArray = [NSMutableArray arrayWithObjects:@"Home", nil];
            textArray2 = [NSMutableArray arrayWithObjects:@"My Profile",@"Community Profile", nil];
        }
        else {
             [_guestLabel setText:@"אורח"];
        textArray = [NSMutableArray arrayWithObjects:@"בית", nil];
            textArray2 = [NSMutableArray arrayWithObjects:@"הפרופיל שלי",@"פרופיל קהילה", nil];
        }
        imageArray2 = [NSMutableArray arrayWithObjects:@"my_profile.png",@"community_profile.png", nil];
        
        
        NSArray* visibleModules = [[LogicServices sharedInstance] getCurrentCommunity].visibleModules;
        for (NSString* moduleName in visibleModules) {
            if ([moduleName isEqualToString:@"Security"]) {
                if ([appLanguage isEqualToString:@"english"]) {
                [textArray addObject:@"Security"];
                }
                else {
                [textArray addObject:@"ביטחון"];
                }
                [imageArray addObject:@"security"];
            }
            if ([moduleName isEqualToString:@"QA"]) {
                if ([appLanguage isEqualToString:@"english"]) {
                    [textArray addObject:@"Q&A"];
                }
                else {
                    [textArray addObject:@"שו״ת"];
                }

                [imageArray addObject:@"question_answers"];
            }
            if ([moduleName isEqualToString:@"Conversation"]) {
                if ([appLanguage isEqualToString:@"english"]) {
                    [textArray addObject:@"Discussions"];
                }
                else {
                    [textArray addObject:@"שיח קהילתי"];
                }

                [imageArray addObject:@"community_conversation"];
            }
            if ([moduleName isEqualToString:@"Support"]) {
                if ([appLanguage isEqualToString:@"english"]) {
                    [textArray2 addObject:@"Support"];
                }
                else {
                    [textArray2 addObject:@"תמיכה"];
                }

                [imageArray2 addObject:@"support"];
            }
            if ([moduleName isEqualToString:@"Facilities"]) {
                if ([appLanguage isEqualToString:@"english"]) {
                    [textArray addObject:@"Facilities"];
                }
                else {
                    [textArray addObject:@"מתקנים"];
                }

                [imageArray addObject:@"commynity_center"];
            }
            if ([moduleName isEqualToString:@"Prayer"]) {
                if ([appLanguage isEqualToString:@"english"]) {
                    [textArray addObject:@"Prayer Times"];
                }
                else {
                    [textArray addObject:@"זמני תפילות"];
                }

                [imageArray addObject:@"clock"];
            }
        }
        [_menuTableView reloadData];
    }
    
    [self.communitiesTable reloadData];
}

- (IBAction)showMyCommunities {
    if (_communitiesTable.alpha == 0)
        [UIView animateWithDuration:0.4 animations:^{
            [_communitiesTable setAlpha:1];
        }];
    else
        [UIView animateWithDuration:0.4 animations:^{
        [_communitiesTable setAlpha:0];
        }];
}

-(void) appLanguageChange {
    [self updateMenuView];
}

-(void)updateMenuWithClick {
    [self tableView:_communitiesTable didSelectRowAtIndexPath:[NSIndexPath indexPathForItem:0 inSection:0]];
}

@end
