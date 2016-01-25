//
//  HomeViewController.m
//  Smart Community
//
//  Created by oren shany on 7/28/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "HomeViewController.h"
#import "UIImageView+AFNetworking.h"
#import "NextEventsCell.h"
#import "SearchNewCommunityViewController.h"
#import "SecurityViewController.h"
#import "QuestionsAndAnswersViewController.h"
#import "CommunityConversationsViewController.h"
#import "PrayersTimeController.h"
#import "CalenderController.h"
#import "Networking.h"

@interface HomeViewController ()

@end

@implementation HomeViewController

NSArray* nextSmallPrayers;
NSArray* lastTwoMessages;
UIRefreshControl *refreshContro;
NSMutableArray* widgetsList;


- (void)viewDidLoad {
    [super viewDidLoad];
    NSString* obj;
    [_table.layer setCornerRadius:6];
    [_table.layer setMasksToBounds:YES];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openCommunitySearch)
                                                 name:@"openCommunitySearch"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reloadData)
                                                 name:@"reloadData"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openGalleryTab)
                                                 name:@"openGalleryTab"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openSecurityReports)
                                                 name:@"openSecurityReports"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(createSecurityReports)
                                                 name:@"createSecurityReports"
                                               object:obj];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(moveToCalender:)
                                                 name:@"moveToCalender"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appLanguageChange)
                                                 name:@"appLanguageChange"
                                               object:obj];

    
    self.client = [PTPusher pusherWithKey:@"4e300240e08bd9fd936b" delegate:self encrypted:YES];
    [self.client connect];
    
    nextSmallPrayers = [[LogicServices sharedInstance] getNextPrayersForDate:[NSDate date]];
    lastTwoMessages = [[LogicServices sharedInstance] getLastTwoMessages];
    [_communityImage.layer setMasksToBounds:YES];
    [_communityImage.layer setCornerRadius:_communityImage.frame.size.width/2];
    if (![DataManagement sharedInstance].currentCommunityID) {
        if ([DataManagement sharedInstance].user.communities.count>0) {
            [DataManagement sharedInstance].currentCommunityID = ((CommunityModel*)[DataManagement sharedInstance].user.communities[0]).communityID;
            [[NSUserDefaults standardUserDefaults] setObject:((CommunityModel*)[DataManagement sharedInstance].user.communities[0]).communityID forKey:@"current_communityID"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            
            [[NSNotificationCenter defaultCenter] postNotificationName:@"updateMenuDetails"
                                                                object:nil
                                                              userInfo:nil];
            
        }
        else {
            SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];
            controller.disableBack = YES;
            [self presentViewController:controller animated:YES completion:nil];
        }
}
    
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    if (community) {
    _communityName.text = community.communityName;
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:community.communityImageUrl]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        
        [_communityImage setImageWithURLRequest:imageRequest
                              placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                       success:nil
                                       failure:nil];
        
    _communityLocation.text = community.locationObj.title;
    }
    widgetsList = community.widgets;
    
    self.table.delegate = self;
    self.table.dataSource = self;
    
    //to add the UIRefreshControl to UIView
    refreshContro = [[UIRefreshControl alloc] init];
    refreshContro.attributedTitle = [[NSAttributedString alloc] initWithString:@""]; //to give the attributedTitle
    [refreshContro addTarget:self action:@selector(refresh:) forControlEvents:UIControlEventValueChanged];
    [self.table addSubview:refreshContro];
    
    [self appLanguageChange];
}


-(void)moveToCalender:(NSNotification *)notification {
    NSDate* selectedDate = notification.object;
    
    UITabBarController *controller = self.menuContainerViewController.centerViewController;
    CalenderController* calenderController = controller.viewControllers[1];
    calenderController.selectedDayFromHome = selectedDate;
    [controller setSelectedIndex:1];
}


- (void)refresh:(UIRefreshControl *)refreshControl
{
    [[Networking sharedInstance] fetchNewData];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(7 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [refreshContro endRefreshing];
    });
}


-(void)openSecurityReports {
    SecurityViewController * report  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"SecurityViewController"];
    [self presentViewController:report animated:YES completion:nil];
}

-(void)createSecurityReports {
    SecurityViewController * report  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"SecurityViewController"];
    report.needToOpenCreateReport = YES;
    [self presentViewController:report animated:YES completion:nil];
}

-(void)reloadData {
    
    if ([[DataManagement sharedInstance] lastCurrentCommunityID]) {
    // unsubscribe to channel
    PTPusherChannel *channel = [self.client channelNamed:[NSString stringWithFormat:@"ch%@" , [DataManagement sharedInstance].lastCurrentCommunityID]];
    [channel unsubscribe];
    }
    
    // subscribe to channel and bind to events
    PTPusherChannel *channel = [self.client channelNamed:[NSString stringWithFormat:@"ch%@" , [DataManagement sharedInstance].currentCommunityID]];
    if (!channel) {
    PTPusherChannel * channel = [self.client subscribeToChannelNamed:[NSString stringWithFormat:@"ch%@" , [DataManagement sharedInstance].currentCommunityID]];
    [channel bindToEventNamed:@"new_message" handleWithBlock:^(PTPusherEvent *channelEvent) {
        // channelEvent.data is a NSDictianary of the JSON object received
        NSString* string = channelEvent.data;
        string = [string stringByReplacingOccurrencesOfString:@"\\" withString:@""];
        NSData* data = [string dataUsingEncoding:NSUTF8StringEncoding];
        NSError* e;
        NSDictionary* dic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&e];
        
        EventOrMessageModel* message = (EventOrMessageModel*)[EventOrMessageModel objectForDictionary:dic];
        message.locationObj = [[LocationModel alloc]init];
        NSDictionary* locationDic = [dic objectForKey:@"location"];
        [message.locationObj setCoords:[locationDic objectForKey:@"coords"]];
        [message.locationObj setTitle:[locationDic objectForKey:@"title"]];
        [message setImageUrl:[dic objectForKey:@"imageURL"]];
        [message setEventID:[dic objectForKey:@"id"]];
        [[LogicServices sharedInstance] addNewMessage:message];
        int badge = [[[self.tabBarController.tabBar.items objectAtIndex:2] badgeValue] intValue];
        badge++;
        [[self.tabBarController.tabBar.items objectAtIndex:2] setBadgeValue:[NSString stringWithFormat:@"%i", badge]];
    }];
    
    [channel bindToEventNamed:@"new_event" handleWithBlock:^(PTPusherEvent *channelEvent) {
        NSMutableArray* newEvents = [NSMutableArray array];
        // channelEvent.data is a NSDictianary of the JSON object received
        NSArray *dailyEventsArr = channelEvent.data;
        for (NSString* eventString in [((NSDictionary*)dailyEventsArr[0]) objectForKey:@"events"]) {
            NSString* string = [eventString stringByReplacingOccurrencesOfString:@"\\" withString:@""];
            NSData* data = [string dataUsingEncoding:NSUTF8StringEncoding];
            NSError* e;
            NSDictionary* dic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&e];

            
            EventOrMessageModel* event = (EventOrMessageModel*)[EventOrMessageModel objectForDictionary:dic];
            event.locationObj = [[LocationModel alloc]init];
            NSDictionary* locationDic = [dic objectForKey:@"location"];
            [event.locationObj setCoords:[locationDic objectForKey:@"coords"]];
            [event.locationObj setTitle:[locationDic objectForKey:@"title"]];
            [event setImageUrl:[dic objectForKey:@"imageURL"]];
            [event setEventID:[dic objectForKey:@"id"]];
            [newEvents addObject:event];
        }
        int badge = [[[self.tabBarController.tabBar.items objectAtIndex:2] badgeValue] intValue];
        badge = badge+newEvents.count;
        [[self.tabBarController.tabBar.items objectAtIndex:2] setBadgeValue:[NSString stringWithFormat:@"%i", badge]];
        [[LogicServices sharedInstance] addNewEvents:newEvents];
    }];
        
    }
    
    
    nextSmallPrayers = [[LogicServices sharedInstance] getNextPrayersForDate:[NSDate date]];
    lastTwoMessages = [[LogicServices sharedInstance] getLastTwoMessages];
    NSMutableArray* communitiesIDS = [NSMutableArray array];
    NSMutableArray* communities = [[DataManagement sharedInstance] user].communities;
    for (CommunityModel* community in communities) {
        [communitiesIDS addObject:community.communityID];
    }
    
    if (communitiesIDS.count>0) {
        //set tags for push targeting
        NSDictionary *tags = [NSDictionary dictionaryWithObjectsAndKeys:
                              communitiesIDS, @"communitiesIDS", nil];
        
        [[PushNotificationManager pushManager] setTags:tags];
    }
    
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    if (community) {
    _communityName.text = community.communityName;
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:community.communityImageUrl]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        
        [_communityImage setImageWithURLRequest:imageRequest
                               placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                        success:nil
                                        failure:nil];
        _communityLocation.text = community.locationObj.title;
        widgetsList = community.widgets;
        
        if ([_table cellForRowAtIndexPath:[NSIndexPath indexPathForItem:0 inSection:0]])
        [_table scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
        
        UITabBarController *controller = self.menuContainerViewController.centerViewController;
        [controller setSelectedIndex:0];
        
        [self.table reloadData];
        [refreshContro endRefreshing];
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {

    if ([widgetsList[indexPath.section] isEqualToString:@"Prayer"]) {
        return 153;
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Announcement"]) {
        return 116;
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Gallery"]) {
        return 100;
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Calendar"]) {
        return 259;
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Security"]) {
        return 78;
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"QA"]) {
        return 193;
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Conversation"]) {
        return 197;
    }
    return 153;
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return widgetsList.count;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if ([widgetsList[section] isEqualToString:@"Announcement"]) {
        return lastTwoMessages.count;;
    }
    else {
        return 1;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
       return 20;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    
    return 10;
}

- (void)tableView:(UITableView *)tableView willDisplayHeaderView:(UIView *)view forSection:(NSInteger)section
{
    // Background color
    view.tintColor = [UIColor whiteColor];
    // Text Color
    UITableViewHeaderFooterView *header = (UITableViewHeaderFooterView *)view;
    [header.textLabel setTextColor:[UIColor grayColor]];
    [header.textLabel setTextAlignment:NSTextAlignmentCenter];
    [header.textLabel setFont:[UIFont fontWithName:@"Roboto-Light" size:13]];
}

- (void)tableView:(UITableView *)tableView willDisplayFooterView:(UIView *)view forSection:(NSInteger)section{
    view.tintColor = [UIColor colorWithRed:237.0f/255.0f green:237.0f/255.0f blue:237.0f/255.0f alpha:1];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
    if ([widgetsList[section] isEqualToString:@"Prayer"]) {
             return @"מניינים קרובים";
    }
    if ([widgetsList[section] isEqualToString:@"Announcement"]) {
        return @"הודעות אחרונות";
    }
    if ([widgetsList[section] isEqualToString:@"Gallery"]) {
        return @"תמונות אחרונות ששותפו";
    }
    if ([widgetsList[section] isEqualToString:@"Calendar"]) {
        return @"אירועים קרובים";
    }
    if ([widgetsList[section] isEqualToString:@"Security"]) {
        return @"דיווח ביטחון";
    }
    if ([widgetsList[section] isEqualToString:@"QA"]) {
        return @"שו״ת";
    }
    if ([widgetsList[section] isEqualToString:@"Conversation"]) {
        return @"שיח קהילתי";
    }
    } else {
        if ([widgetsList[section] isEqualToString:@"Prayer"]) {
            return @"Next Services";
        }
        if ([widgetsList[section] isEqualToString:@"Announcement"]) {
            return @"Recent News & Announcements";
        }
        if ([widgetsList[section] isEqualToString:@"Gallery"]) {
            return @"Recently Uploaded to Gallery";
        }
        if ([widgetsList[section] isEqualToString:@"Calendar"]) {
            return @"Next Events";
        }
        if ([widgetsList[section] isEqualToString:@"Security"]) {
            return @"Security Report";
        }
        if ([widgetsList[section] isEqualToString:@"QA"]) {
            return @"Q&A";
        }
        if ([widgetsList[section] isEqualToString:@"Conversation"]) {
            return @"Discussions";
        }
    }
    
    return @"";
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([widgetsList[indexPath.section] isEqualToString:@"Prayer"]) {
    nextServicesCell *cell = [tableView dequeueReusableCellWithIdentifier:@"nextServicesCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"nextServicesCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        
        [cell.firstPrayer setText:@""];
        [cell.secondPrayer setText:@""];
        [cell.thirdPrayer setText:@""];
        
        for (SmallPrayer* prayer in nextSmallPrayers) {
            if ([prayer.type isEqualToString:@"morning"]) {
                if ([cell.firstPrayer.text isEqualToString:@""]) {
                    [cell.firstPrayer setText:prayer.time];
                }
                else {
                    [cell.firstPrayer setText:[NSString stringWithFormat:@"%@, %@", cell.firstPrayer.text, prayer.time]];
                }
            }
            if ([prayer.type isEqualToString:@"mincha"]) {
                if ([cell.secondPrayer.text isEqualToString:@""]) {
                    [cell.secondPrayer setText:prayer.time];
                }
                else {
                    [cell.secondPrayer setText:[NSString stringWithFormat:@"%@, %@", cell.secondPrayer.text, prayer.time]];
                }
            }
            if ([prayer.type isEqualToString:@"evening"]) {
                if ([cell.thirdPrayer.text isEqualToString:@""]) {
                    [cell.thirdPrayer setText:prayer.time];
                }
                else {
                    [cell.thirdPrayer setText:[NSString stringWithFormat:@"%@, %@", cell.thirdPrayer.text, prayer.time]];
                }
            }
            
        }
    
        return cell;
    }
    else {
        if ([widgetsList[indexPath.section] isEqualToString:@"Announcement"]) {
            messageCell *cell = [tableView dequeueReusableCellWithIdentifier:@"messageCell"];
            if (cell == nil) {
                // Load the top-level objects from the custom cell XIB.
                NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"messageCell" owner:self options:nil];
                // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                cell = [topLevelObjects objectAtIndex:0];
            }
            [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
            
            if (lastTwoMessages.count>indexPath.row) {
            cell.messageTitle.text = ((EventOrMessageModel*)lastTwoMessages[indexPath.row]).name;
            cell.messageContent.text = ((EventOrMessageModel*)lastTwoMessages[indexPath.row]).content;
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
            [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
            NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
            [dateFormatter setTimeZone:tz];
            NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:((EventOrMessageModel*)lastTwoMessages[indexPath.row]).sTime/1000]];
            cell.cDate.text = formattedDateString;
            NSURL *url = [NSURL URLWithString:((EventOrMessageModel*)lastTwoMessages[indexPath.row]).imageUrl];            ;
            NSURLRequest *imageRequest = [NSURLRequest requestWithURL:url
                                                          cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                      timeoutInterval:60];
            
            [cell.messageImage setImageWithURLRequest:imageRequest
                                   placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                            success:nil
                                            failure:nil];
            }

            return cell;
        }
        else {
            if ([widgetsList[indexPath.section] isEqualToString:@"Gallery"]) {
                UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"RecentImagesCell"];
                if (cell == nil) {
                    // Load the top-level objects from the custom cell XIB.
                    NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"RecentImagesCell" owner:self options:nil];
                    // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                    cell = [topLevelObjects objectAtIndex:0];
                }
                return  cell;
            }
            else {
                if ([widgetsList[indexPath.section] isEqualToString:@"Calendar"]) {
                    NextEventsCell *cell = [tableView dequeueReusableCellWithIdentifier:@"NextEventsCell"];
                    if (cell == nil) {
                        // Load the top-level objects from the custom cell XIB.
                        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"NextEventsCell" owner:self options:nil];
                        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                        cell = [topLevelObjects objectAtIndex:0];
                    }
                    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
                    return  cell;
                }
                else {
                    if ([widgetsList[indexPath.section] isEqualToString:@"Security"]) {
                        UITableViewCell *cell;
                            NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"SecurityReportCell" owner:self options:nil];
                            // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                            cell = [topLevelObjects objectAtIndex:0];
                        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
                        return  cell;
                    }
                    else {
                        if ([widgetsList[indexPath.section] isEqualToString:@"QA"]) {
                            QuestionCell *cell = [tableView dequeueReusableCellWithIdentifier:@"QuestionCell"];
                            if (cell == nil) {
                                // Load the top-level objects from the custom cell XIB.
                                NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"QuestionCell" owner:self options:nil];
                                // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                                cell = [topLevelObjects objectAtIndex:0];
                            }
                            [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
                            [cell initWithQuestion:[[LogicServices sharedInstance] getLastPublicQuestion]];
                            return  cell;
                        } else {
                            if ([widgetsList[indexPath.section] isEqualToString:@"Conversation"]) {
                                CommunityPostCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CommunityPostCell"];
                                if (cell == nil) {
                                    // Load the top-level objects from the custom cell XIB.
                                    NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"CommunityPostCell" owner:self options:nil];
                                    // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                                    cell = [topLevelObjects objectAtIndex:0];
                                }
                                [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
                                [cell initWithPost:[[LogicServices sharedInstance] getLastCommunityPost]];
                                return  cell;
                            }

                        }

                    }

                }

            }

        }

    }
    UITableViewCell* cell = [[UITableViewCell alloc]init];
        return  cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([widgetsList[indexPath.section] isEqualToString:@"Announcement"]) {
        UITabBarController *controller = self.menuContainerViewController.centerViewController;
        [controller setSelectedIndex:2];
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Security"]) {
        SecurityViewController * conversations  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"SecurityViewController"];
        [self presentViewController:conversations animated:YES completion:nil];
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"QA"]) {
        QuestionsAndAnswersViewController *questions = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"QuestionsAndAnswersViewController"];
        [self presentViewController:questions animated:YES completion:nil];
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Conversation"]) {
        CommunityConversationsViewController * conversations  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"CommunityConversationsViewController"];
        [self presentViewController:conversations animated:YES completion:nil];
    }
    if ([widgetsList[indexPath.section] isEqualToString:@"Prayer"]) {
        PrayersTimeController * prayerTimes  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"PrayersTimeController"];
        [self presentViewController:prayerTimes animated:YES completion:nil];
    }
}

-(void) openGalleryTab{
    UITabBarController *controller = self.menuContainerViewController.centerViewController;
    [controller setSelectedIndex:3];
}

-(void)openCommunitySearch {
    [self.menuContainerViewController setMenuState:MFSideMenuStateClosed completion:^{}];
    
    SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

-(void) appLanguageChange {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_tabTitle setText:@"Home"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"בית"];
    }
    
    [_table reloadData];
}




- (IBAction)menuAction:(id)sender
{
    [self.menuContainerViewController setMenuState:MFSideMenuStateLeftMenuOpen completion:^{}];
}
@end
