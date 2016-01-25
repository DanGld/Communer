//
//  CalenderController.m
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CalenderController.h"
#import "UIImageView+AFNetworking.h"
#import "SearchNewCommunityViewController.h"
#import "PrayersTimeController.h"


@interface CalenderController () {
    NSMutableDictionary *_eventsByDate;
    
    NSDate *_todayDate;
    NSDate *_minDate;
    NSDate *_maxDate;
    
    NSDate *_dateSelected;
}

@end

@implementation CalenderController

NSArray* eventsArr;
NSMutableArray* allEventsForSelectedDay;
BOOL prayersFilterEnable;
BOOL eventsFilterEnable;
BOOL activitiesFilterEnable;
VCFloatingActionButton* floatingButton;

- (void)viewDidLoad
{
    [super viewDidLoad];
    NSString* obj;
    prayersFilterEnable = YES;
    eventsFilterEnable = YES;
    activitiesFilterEnable = YES;
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openCommunitySearch)
                                                 name:@"openCommunitySearch"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(createEventsAndReloadData)
                                                 name:@"reloadData"
                                               object:obj];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appLanguageChange)
                                                 name:@"appLanguageChange"
                                               object:obj];

   
    _calendarManager = [JTCalendarManager new];
    _calendarManager.delegate = self;
    
    // Generate random events sort by date using a dateformatter for the demonstration
    [self createEvents];
    
    // Create a min and max date for limit the calendar, optional
    [self createMinAndMaxDate];
    
    [_calendarManager setMenuView:_calendarMenuView];
    [_calendarManager setContentView:_calendarContentView];
    [_calendarManager setDate:_todayDate];
    _calendarManager.settings.weekModeEnabled = YES;
    [_calendarManager reload];
    _eventsTable.delegate = self;
    _eventsTable.dataSource = self;
    
    
    [_communityImage.layer setMasksToBounds:YES];
    [_communityImage.layer setCornerRadius:_communityImage.frame.size.width/2];
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    if (community) {
    _communityTitle.text = community.communityName;
    NSURL *url = [[NSURL alloc] initWithString:community.communityImageUrl];
        UIImageView* imageView = [[UIImageView alloc]init];
        [imageView setImageWithURL:[NSURL URLWithString:community.communityImageUrl]];
    [_communityImage setImageWithURL:url placeholderImage:imageView.image];
    _communityLocation.text = community.locationObj.title;
    }
    
    floatingButton = [[VCFloatingActionButton alloc]initWithFrame:_filterButton.frame normalImage:[UIImage imageNamed:@"filterButton"] andPressedImage:[UIImage imageNamed:@"filterButton"] withScrollview:_eventsTable];
    
    floatingButton.imageArray = @[@"activites",@"events",@"prayer"];
    floatingButton.labelArray = @[@"Activities",@"Important events",@"Prayers"];
    [floatingButton setDelegate:self];
    [self.view addSubview:floatingButton];
    [self appLanguageChange];
}

-(void)didSelectMenuOptionAtIndex:(NSInteger)row {
    if (row==0) {
        activitiesFilterEnable = !activitiesFilterEnable;
    }
    
    if (row==1) {
        eventsFilterEnable = !eventsFilterEnable;
    }
    
    if (row==2) {
            prayersFilterEnable = !prayersFilterEnable;
    }
    
    floatingButton.imageArray = @[activitiesFilterEnable ? @"activites" : @"activities_disabled",eventsFilterEnable ? @"events" : @"events_disabled", prayersFilterEnable ? @"prayer" : @"prayer_disabled"];
    
    [_eventsTable reloadData];
}

-(void)viewWillAppear:(BOOL)animated {
    if (_selectedDayFromHome) {
        _dateSelected = _selectedDayFromHome;
        [_calendarContentView setDate:_dateSelected];
       [_calendarManager reload];
        [_eventsTable reloadData];
        _selectedDayFromHome = nil;
    }
}

-(void)createEventsAndReloadData {
    [self createEvents];
    [_calendarManager reload];
    CommunityModel* community = [[LogicServices sharedInstance] getCurrentCommunity];
    if (community) {
    _communityTitle.text = community.communityName;
    NSURL *url = [[NSURL alloc] initWithString:community.communityImageUrl];
        UIImageView* imageView = [[UIImageView alloc]init];
        [imageView setImageWithURL:[NSURL URLWithString:community.communityImageUrl]];
    [_communityImage setImageWithURL:url placeholderImage:imageView.image];
    _communityLocation.text = community.locationObj.title;
    [_eventsTable reloadData];
    }
}


#pragma mark - Table callback

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(_dateSelected) {
        dailyEventsModel* dailyEvents = [[LogicServices sharedInstance] getCommunityDailyEventsForDate:_dateSelected];
        if (dailyEvents) {
            allEventsForSelectedDay = [NSMutableArray array];
            
            if (eventsFilterEnable)
            [allEventsForSelectedDay addObjectsFromArray:dailyEvents.events];
            
            if (activitiesFilterEnable) {
            for (FacilityOrActivityModel* activity in dailyEvents.activities) {
                [allEventsForSelectedDay addObjectsFromArray:activity.events];
                    }
            }
            
            if (prayersFilterEnable) {
            for (PrayerDetailsModel* prayerDetails in dailyEvents.prayers) {
                //[allEventsForSelectedDay addObjectsFromArray:prayerDetails.prayers];
                [allEventsForSelectedDay addObjectsFromArray:prayerDetails.smallPrayers];
                    }
            }
            
            allEventsForSelectedDay = [NSMutableArray arrayWithArray:[allEventsForSelectedDay sortedArrayUsingComparator: ^(EventOrMessageModel* m1, EventOrMessageModel* m2) {
                    return [[NSDate dateWithTimeIntervalSince1970:m1.date/1000] compare:[NSDate dateWithTimeIntervalSince1970:m2.date/1000]];
            }]];
            
             
            return allEventsForSelectedDay.count;
        }
        else {
            return 0;
        }
    }
    else {
        dailyEventsModel* dailyEvents = [[LogicServices sharedInstance] getCommunityDailyEventsForDate:[NSDate date]];
        if (dailyEvents) {
            allEventsForSelectedDay = [NSMutableArray array];
            
            if (eventsFilterEnable)
            [allEventsForSelectedDay addObjectsFromArray:dailyEvents.events];
            
            if (activitiesFilterEnable) {
            for (FacilityOrActivityModel* activity in dailyEvents.activities) {
                [allEventsForSelectedDay addObjectsFromArray:activity.events];
                    }
            }
            
            if (prayersFilterEnable) {
            for (PrayerDetailsModel* prayerDetails in dailyEvents.prayers) {
                //[allEventsForSelectedDay addObjectsFromArray:prayerDetails.prayers];
                [allEventsForSelectedDay addObjectsFromArray:prayerDetails.smallPrayers];
                    }
            }
            
            allEventsForSelectedDay = [NSMutableArray arrayWithArray:[allEventsForSelectedDay sortedArrayUsingComparator: ^(EventOrMessageModel* m1, EventOrMessageModel* m2) {
                return [[NSDate dateWithTimeIntervalSince1970:m1.date/1000] compare:[NSDate dateWithTimeIntervalSince1970:m2.date/1000]];
            }]];
            
            return allEventsForSelectedDay.count;
        }
        else {
            return 0;
        }

    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CalenderEventCell *cell;// = [tableView dequeueReusableCellWithIdentifier:@"CalenderEventCell"];
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"CalenderEventCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }

    id obj = allEventsForSelectedDay[indexPath.row];  //event
    if ([obj isKindOfClass:[EventOrMessageModel class]]) {
        EventOrMessageModel* event = obj;
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"HH:mm"];
        NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
        [formatter setTimeZone:tz];
        NSString *startTimeString = [formatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:event.sTime/1000] ];
        NSString *endTimeString = [formatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:event.eTime/1000] ];
        
        cell.eventTime.text = [NSString stringWithFormat:@"%@ - %@" ,startTimeString, endTimeString];
        cell.eventTitle.text = event.name;
        cell.eventAddress.text = event.locationObj.title;
        cell.prayerImageTime.hidden = YES;
        if ([event.type isEqualToString:@"activity"]) {
            cell.bgImage.image = [UIImage imageNamed:@"activites_bg.png"];
        } else {
            cell.bgImage.image = [UIImage imageNamed:@"important_event_bg.png"];
        }
        return cell;
    }
    
    if ([obj isKindOfClass:[SmallPrayer class]]) {  //prayer
        SmallPrayer* prayer = obj;
        cell.eventTime.text = prayer.time;
        cell.eventTitle.text = prayer.title;
        cell.eventAddress.text = @"";
        cell.prayerImageTime.hidden = NO;
        cell.prayerImageTime.image = [self setPrayerImage:prayer];
        UIImage* image = [UIImage imageNamed:@"prayers_bg.png"];
        cell.bgImage.image = image;
        return cell;
    }
    return cell;
}


#pragma mark - Buttons callback

- (IBAction)didGoTodayTouch
{
    [_calendarManager setDate:_todayDate];
}

- (IBAction)didChangeModeTouch
{
    _calendarManager.settings.weekModeEnabled = !_calendarManager.settings.weekModeEnabled;
    [_calendarManager reload];
    
    CGFloat newHeight = 300;
    if(_calendarManager.settings.weekModeEnabled){
        newHeight = 85.;
    }
    
    self.calendarContentViewHeight.constant = newHeight;
    [self.view layoutIfNeeded];
}

#pragma mark - CalendarManager delegate

// Exemple of implementation of prepareDayView method
// Used to customize the appearance of dayView
- (void)calendar:(JTCalendarManager *)calendar prepareDayView:(JTCalendarDayView *)dayView
{
    // Today
    if([_calendarManager.dateHelper date:[NSDate date] isTheSameDayThan:dayView.date]){
        dayView.circleView.hidden = NO;
        dayView.circleView.backgroundColor = [UIColor colorWithRed:19.0f/255.0f green:150.0f/255.0f blue:241.0f/255.0f alpha:1];
        dayView.dot1View.backgroundColor = [UIColor whiteColor];
        dayView.dot2View.backgroundColor = [UIColor whiteColor];
        dayView.dot3View.backgroundColor = [UIColor whiteColor];
        dayView.textLabel.textColor = [UIColor whiteColor];
    }
    // Selected date
    else if(_dateSelected && [_calendarManager.dateHelper date:_dateSelected isTheSameDayThan:dayView.date]){
        dayView.circleView.hidden = NO;
        dayView.circleView.backgroundColor = [UIColor lightGrayColor];
        dayView.dot1View.backgroundColor = [UIColor whiteColor];
        dayView.dot2View.backgroundColor = [UIColor whiteColor];
        dayView.dot3View.backgroundColor = [UIColor whiteColor];
        dayView.textLabel.textColor = [UIColor whiteColor];
    }
    // Other month
    else if(![_calendarManager.dateHelper date:_calendarContentView.date isTheSameMonthThan:dayView.date]){
        dayView.circleView.hidden = YES;
        dayView.dot1View.backgroundColor = [UIColor redColor];
        dayView.dot2View.backgroundColor = [UIColor greenColor];
        dayView.dot3View.backgroundColor = [UIColor blueColor];
        dayView.textLabel.textColor = [UIColor lightGrayColor];
    }
    // Another day of the current month
    else{
        dayView.circleView.hidden = YES;
        dayView.dot1View.backgroundColor = [UIColor redColor];
        dayView.dot2View.backgroundColor = [UIColor greenColor];
        dayView.dot3View.backgroundColor = [UIColor blueColor];
        dayView.textLabel.textColor = [UIColor blackColor];
    }
    
    if([self havePrayersForDay:dayView.date]){
        dayView.dot1View.hidden = NO;
    }
    else{
        dayView.dot1View.hidden = YES;
    }
    
    if([self haveEventForDay:dayView.date]){
        dayView.dot2View.hidden = NO;
    }
    else{
        dayView.dot2View.hidden = YES;
    }
    
    if([self haveActivitiesForDay:dayView.date]){
        dayView.dot3View.hidden = NO;
    }
    else{
        dayView.dot3View.hidden = YES;
    }
}

- (void)calendar:(JTCalendarManager *)calendar didTouchDayView:(JTCalendarDayView *)dayView
{
    _dateSelected = dayView.date;
    
    // Animation for the circleView
    dayView.circleView.transform = CGAffineTransformScale(CGAffineTransformIdentity, 0.1, 0.1);
    [UIView transitionWithView:dayView
                      duration:.3
                       options:0
                    animations:^{
                        dayView.circleView.transform = CGAffineTransformIdentity;
                        [_calendarManager reload];
                    } completion:nil];
    
        
    
    [_eventsTable reloadData];
    
    
}

#pragma mark - CalendarManager delegate - Page mangement

// Used to limit the date for the calendar, optional
- (BOOL)calendar:(JTCalendarManager *)calendar canDisplayPageWithDate:(NSDate *)date
{
    return [_calendarManager.dateHelper date:date isEqualOrAfter:_minDate andEqualOrBefore:_maxDate];
}

- (void)calendarDidLoadNextPage:(JTCalendarManager *)calendar
{
    //    NSLog(@"Next page loaded");
}

- (void)calendarDidLoadPreviousPage:(JTCalendarManager *)calendar
{
    //    NSLog(@"Previous page loaded");
}

#pragma mark - Fake data

- (void)createMinAndMaxDate
{
    _todayDate = [NSDate date];
    
    // Min date will be 2 month before today
    _minDate = [_calendarManager.dateHelper addToDate:_todayDate months:-2];
    
    // Max date will be 2 month after today
    _maxDate = [_calendarManager.dateHelper addToDate:_todayDate months:2];
}

// Used only to have a key for _eventsByDate
- (NSDateFormatter *)dateFormatter
{
    static NSDateFormatter *dateFormatter;
    if(!dateFormatter){
        dateFormatter = [NSDateFormatter new];
        dateFormatter.dateFormat = @"dd-MM-yyyy";
        NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
        [dateFormatter setTimeZone:tz];
    }
    
    return dateFormatter;
}

- (BOOL)haveEventForDay:(NSDate *)date
{
    dailyEventsModel* dailyEvents = [[LogicServices sharedInstance] getCommunityDailyEventsForDate:date];
    if (dailyEvents.events.count > 0) {
        return YES;
    }
    return NO;
}

- (BOOL)haveActivitiesForDay:(NSDate *)date
{
    dailyEventsModel* dailyEvents = [[LogicServices sharedInstance] getCommunityDailyEventsForDate:date];
    if (dailyEvents.activities.count > 0) {
        return YES;
    }
    return NO;
}

- (BOOL)havePrayersForDay:(NSDate *)date
{
    dailyEventsModel* dailyEvents = [[LogicServices sharedInstance] getCommunityDailyEventsForDate:date];
    if (dailyEvents.prayers.count > 0) {
        for (PrayerDetailsModel* prayerDetails in dailyEvents.prayers) {
            if (prayerDetails.smallPrayers.count>0) {
                return YES;
            }
        }
    }
    return NO;
}

- (void)createEvents
{
    _eventsByDate = [NSMutableDictionary new];
    NSArray* events = [[LogicServices sharedInstance] getAllCommunityEvents];
    for (EventOrMessageModel* event in events) {
        NSString *key = [[self dateFormatter] stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:event.date/1000] ];
        if(!_eventsByDate[key]){
            _eventsByDate[key] = [NSMutableArray new];
        }
        [_eventsByDate[key] addObject:[[NSDate alloc]initWithTimeIntervalSince1970:event.date/1000] ];
    }
}

- (IBAction)menuAction:(id)sender
{
    [self.menuContainerViewController setMenuState:MFSideMenuStateLeftMenuOpen completion:^{}];
}

- (IBAction)rightArrowClicked {
    [_calendarContentView loadNextPageWithAnimation];
}

- (IBAction)leftArrowClicked {
    [_calendarContentView loadPreviousPageWithAnimation];
}


-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 110;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    id obj = allEventsForSelectedDay[indexPath.row];  //event
    if ([obj isKindOfClass:[EventOrMessageModel class]]) {
        EventOrMessageModel* event = obj;
        SingleEventViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SingleEventViewController"];
        controller.event = event;
        [self presentViewController:controller animated:YES completion:nil];
    }
    if ([obj isKindOfClass:[SmallPrayer class]]) {
        PrayersTimeController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"PrayersTimeController"];
        [self presentViewController:controller animated:YES completion:nil];
    }
}

-(void)openCommunitySearch {
    [self.menuContainerViewController setMenuState:MFSideMenuStateClosed];
    SearchNewCommunityViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SearchNewCommunityViewController"];
    [self.navigationController pushViewController:controller animated:YES];
}

-(UIImage *)setPrayerImage:(SmallPrayer *)prayer
{
    if ([prayer.type isEqualToString:@"morning"]) {
        return [UIImage imageNamed:@"morning_service.png"];
    }
    if ([prayer.type isEqualToString:@"mincha"]) {
        return [UIImage imageNamed:@"mincha.png"];
    }
    if ([prayer.type isEqualToString:@"evening"]) {
        return [UIImage imageNamed:@"evning.png"];
    }
    return [UIImage imageNamed:@"magen_david.png"];
}

-(void) appLanguageChange {
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"english"]) {
        [_tabTitle setText:@"Calendar"];
    }
    if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"יומן"];
    }
    
}



@end
