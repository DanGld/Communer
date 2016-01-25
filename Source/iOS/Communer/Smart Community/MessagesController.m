//
//  MessagesController.m
//  Smart Community
//
//  Created by oren shany on 8/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "MessagesController.h"
#import "UIImageView+AFNetworking.h"
#import "SearchNewCommunityViewController.h"
#import <Pusher/Pusher.h>
#import "Networking.h"


@interface MessagesController ()

@end

@implementation MessagesController

NSArray* personalMessagesArr;
NSArray* generalMessagesArr;
NSArray* eventsArray;
BOOL isPopupHidden;
UIRefreshControl *refreshControl;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    
    isPopupHidden = YES;
    NSString* obj;

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openCommunitySearch)
                                                 name:@"openCommunitySearch"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(reloadData)
                                                 name:@"reloadData"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appLanguageChange)
                                                 name:@"appLanguageChange"
                                               object:obj];

    
    personalMessagesArr = [[LogicServices sharedInstance] getAllPersonalMessages];
    generalMessagesArr = [[LogicServices sharedInstance] getAllGeneralMessages];
    eventsArray = [[LogicServices sharedInstance] getAllCommunityEvents];
    
    self.table.delegate = self;
    self.table.dataSource = self;
    
    //to add the UIRefreshControl to UIView
     refreshControl = [[UIRefreshControl alloc] init];
    refreshControl.attributedTitle = [[NSAttributedString alloc] initWithString:@""]; //to give the attributedTitle
    [refreshControl addTarget:self action:@selector(refresh:) forControlEvents:UIControlEventValueChanged];
    [self.table addSubview:refreshControl];
    
    [_communityImage.layer setMasksToBounds:YES];
    [_communityImage.layer setCornerRadius:_communityImage.frame.size.width/2];
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    _communityName.text = community.communityName;
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:community.communityImageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [_communityImage setImageWithURLRequest:imageRequest
                           placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                    success:nil
                                    failure:nil];

    _communityLocation.text = community.locationObj.title;
    
    [self appLanguageChange];
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[self.tabBarController.tabBar.items objectAtIndex:2] setBadgeValue:nil];
}
- (void)refresh:(UIRefreshControl *)refreshControl
{
    [[Networking sharedInstance] fetchNewData];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(7 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [refreshControl endRefreshing];
    });
}

-(void)reloadData {
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    _communityName.text = community.communityName;
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:community.communityImageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [_communityImage setImageWithURLRequest:imageRequest
                           placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                    success:nil
                                    failure:nil];

    _communityLocation.text = community.locationObj.title;
    
    personalMessagesArr = [[LogicServices sharedInstance] getAllPersonalMessages];
    generalMessagesArr = [[LogicServices sharedInstance] getAllGeneralMessages];
    eventsArray = [[LogicServices sharedInstance] getAllCommunityEvents];
    [self.table reloadData];
    [refreshControl endRefreshing];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 3;
}



-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section==0) {
        return personalMessagesArr.count;
    }
    if (section==1) {
        return generalMessagesArr.count;
    }
    if (section==2) {
        return eventsArray.count;
    }
    return 0;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    NSString *sectionName;
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        switch (section)
        {
            case 0:
                sectionName = @"Personal";
                break;
            case 1:
                sectionName = @"General";
                break;
            case 2:
                sectionName = @"Events";
                break;
        }
    }
    if ([language isEqualToString:@"hebrew"]) {
        switch (section)
        {
            case 0:
                sectionName = @"אישי";
                break;
            case 1:
                sectionName = @"כללי";
                break;
            case 2:
                sectionName = @"אירועים";
                break;
        }
    }

       return sectionName;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section==2) { // events
        return 126;
    }
    else { // messages
        return 116;
    }
    return 126;
}

- (void)tableView:(UITableView *)tableView willDisplayHeaderView:(UIView *)view forSection:(NSInteger)section
{
    // Background color
    view.tintColor = [UIColor colorWithRed:237.0f/255.0f green:237.0f/255.0f blue:237.0f/255.0f alpha:1];
    
    // Text Color
    UITableViewHeaderFooterView *header = (UITableViewHeaderFooterView *)view;
    [header.textLabel setTextColor:[UIColor grayColor]];
    [header.textLabel setFont:[UIFont fontWithName:@"Roboto-Light" size:13]];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section==2) {
        EventCell *cell = [tableView dequeueReusableCellWithIdentifier:@"EventCell"];
        if (cell == nil) {
            // Load the top-level objects from the custom cell XIB.
            NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"EventCell" owner:self options:nil];
            // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
            cell = [topLevelObjects objectAtIndex:0];
        }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        
        cell.eventAddress.text = ((EventOrMessageModel*)eventsArray[indexPath.row]).locationObj.title;
        
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
        [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
        NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
        [dateFormatter setTimeZone:tz];
        NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:((EventOrMessageModel*)eventsArray[indexPath.row]).sTime/1000]];
        cell.eventDate.text = formattedDateString;
        
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"HH:mm"];
        [formatter setTimeZone:tz];
        NSString *startTimeString = [formatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:((EventOrMessageModel*)eventsArray[indexPath.row]).sTime/1000]];
        NSString *endTimeString = [formatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:((EventOrMessageModel*)eventsArray[indexPath.row]).eTime/1000]];
        cell.eventTime.text = [NSString stringWithFormat:@"%@-%@" , startTimeString, endTimeString];
        cell.eventTitle.text = ((EventOrMessageModel*)eventsArray[indexPath.row]).name;
        NSString* stringUrl = [self createMapUrlWithCoords:((EventOrMessageModel*)eventsArray[indexPath.row]).locationObj.coords];
        NSURL *mapUrl = [NSURL URLWithString:[self URLEncodeString:stringUrl]];
        NSURLRequest *requestObj = [NSURLRequest requestWithURL:mapUrl];
        [cell.mapWebView loadRequest:requestObj];
        return cell;

    }
    else {
        if (indexPath.section==0) {
        messageCell *cell = [tableView dequeueReusableCellWithIdentifier:@"messageCell"];
        if (cell == nil) {
            // Load the top-level objects from the custom cell XIB.
            NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"messageCell" owner:self options:nil];
            // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
            cell = [topLevelObjects objectAtIndex:0];
        }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
        [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
            NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
            [dateFormatter setTimeZone:tz];
        NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:((EventOrMessageModel*)personalMessagesArr[indexPath.row]).sTime/1000]];
        cell.cDate.text = formattedDateString;
        cell.messageContent.text = ((EventOrMessageModel*)personalMessagesArr[indexPath.row]).content;
        cell.messageTitle.text = ((EventOrMessageModel*)personalMessagesArr[indexPath.row]).name;
        ;
        NSURL *url = [NSURL URLWithString:((EventOrMessageModel*)personalMessagesArr[indexPath.row]).imageUrl];
            NSURLRequest *imageRequest = [NSURLRequest requestWithURL:url
                                                          cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                      timeoutInterval:60];
            
            [cell.messageImage setImageWithURLRequest:imageRequest
                                   placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                            success:nil
                                            failure:nil];

        return cell;
        } else {
            messageCell *cell = [tableView dequeueReusableCellWithIdentifier:@"messageCell"];
            if (cell == nil) {
                // Load the top-level objects from the custom cell XIB.
                NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"messageCell" owner:self options:nil];
                // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                cell = [topLevelObjects objectAtIndex:0];
            }
            [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
            
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
            [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
            NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
            [dateFormatter setTimeZone:tz];
            NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:((EventOrMessageModel*)generalMessagesArr[indexPath.row]).sTime/1000]];
            cell.cDate.text = formattedDateString;
            cell.messageContent.text = ((EventOrMessageModel*)generalMessagesArr[indexPath.row]).content;
            cell.messageTitle.text = ((EventOrMessageModel*)generalMessagesArr[indexPath.row]).name;
            ;
            NSURL *url = [NSURL URLWithString:((EventOrMessageModel*)generalMessagesArr[indexPath.row]).imageUrl];
            NSURLRequest *imageRequest = [NSURLRequest requestWithURL:url
                                                          cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                      timeoutInterval:60];
            
            [cell.messageImage setImageWithURLRequest:imageRequest
                                     placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                              success:nil
                                              failure:nil];
            return cell;
        }
    }
    }

-(NSString*)createMapUrlWithCoords:(NSString*)coords {
    if ([coords isEqualToString:@""]) { //put community location
        coords = [[LogicServices sharedInstance] getCurrentCommunity].locationObj.coords;
    }
    return [NSString stringWithFormat:@"https://maps.googleapis.com/maps/api/staticmap?center=%@&zoom=16&size=350x170&maptype=roadmap&markers=color:red|%@" , coords, coords];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    if (!isPopupHidden) {
        [UIView animateWithDuration:0.3 animations:^{
            [_blackCoverView setAlpha:0];
            [_messagePopupView setAlpha:0];
        }];
        isPopupHidden = YES;
    }
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        EventOrMessageModel* message = (EventOrMessageModel*)personalMessagesArr[indexPath.row];
        [_popupTitle setText:message.name];
        [_popupTextView setText:message.content];
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
        [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
        NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
        [dateFormatter setTimeZone:tz];
        NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:message.sTime/1000]];
        [_popupDate setText:formattedDateString];
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:message.imageUrl]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        
        [_popupImage setImageWithURLRequest:imageRequest
                                 placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                          success:nil
                                          failure:nil];

        [UIView animateWithDuration:0.3 animations:^{
            [_blackCoverView setAlpha:0.5];
            [_messagePopupView setAlpha:1];
        }];
        isPopupHidden = NO;
    }
    if (indexPath.section == 1) {
        EventOrMessageModel* message = (EventOrMessageModel*)generalMessagesArr[indexPath.row];
        [_popupTitle setText:message.name];
        [_popupTextView setText:message.content];
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
        [dateFormatter setTimeStyle:NSDateFormatterShortStyle];
        NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
        [dateFormatter setTimeZone:tz];
        NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:message.sTime/1000]];
        [_popupDate setText:formattedDateString];
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:message.imageUrl]
                                                      cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                                  timeoutInterval:60];
        
        [_popupImage setImageWithURLRequest:imageRequest
                           placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                    success:nil
                                    failure:nil];

        [UIView animateWithDuration:0.3 animations:^{
            [_blackCoverView setAlpha:0.5];
            [_messagePopupView setAlpha:1];
        }];
        isPopupHidden = NO;

    }
    if (indexPath.section == 2) {
        SingleEventViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SingleEventViewController"];
        controller.event = (EventOrMessageModel*)eventsArray[indexPath.row];
        [self presentViewController:controller animated:YES completion:nil];
    }
}

- (IBAction)menuAction:(id)sender
{
    [self.menuContainerViewController setMenuState:MFSideMenuStateLeftMenuOpen completion:^{}];
}

-(NSString *) URLEncodeString:(NSString *) str
{
    
    NSMutableString *tempStr = [NSMutableString stringWithString:str];
    [tempStr replaceOccurrencesOfString:@" " withString:@"+" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tempStr length])];
    
    
    return [[NSString stringWithFormat:@"%@",tempStr] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}

-(void)openCommunitySearch {
    [self.menuContainerViewController setMenuState:MFSideMenuStateClosed];
    SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

-(void) appLanguageChange {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_tabTitle setText:@"Messages"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"הודעות"];
    }
    [_table reloadData];
}


@end
