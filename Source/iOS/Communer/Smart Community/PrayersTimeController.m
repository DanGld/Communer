//
//  PrayersTimeController.m
//  Smart Community
//
//  Created by oren shany on 8/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "PrayersTimeController.h"
#import "UIImageView+AFNetworking.h"
#import "SingleEventViewController.h"
#import "SingleActivitieViewController.h"


@interface PrayersTimeController () {
    NSMutableDictionary *_eventsByDate;
    
    NSDate *_todayDate;
    NSDate *_minDate;
    NSDate *_maxDate;
    
    NSDate *_dateSelected;
}

@end

@implementation PrayersTimeController

NSMutableArray* popupServices;
NSArray* additions;
NSArray* smallPrayers;


- (void)viewDidLoad {
    [super viewDidLoad];
    popupServices = [NSMutableArray array];
    [self.servicesTable setDelegate:self];
    [self.servicesTable setDataSource:self];
    [self.additionsTable setDelegate:self];
    [self.additionsTable setDataSource:self];
    [self.smallPrayersTable setDelegate:self];
    [self.smallPrayersTable setDataSource:self];
    
    _dateSelected = [NSDate date];
    [_scrollView setContentSize:CGSizeMake([[UIScreen mainScreen] bounds].size.width, 619)];
    [self initData:[NSDate date]];
    _calendarManager = [JTCalendarManager new];
    _calendarManager.delegate = self;
    
    // Generate random events sort by date using a dateformatter for the demonstration
    //[self createEvents];
    
    // Create a min and max date for limit the calendar, optional
    [self createMinAndMaxDate];
    
    [_calendarManager setMenuView:_calendarMenuView];
    [_calendarManager setContentView:_calendarContentView];
    [_calendarManager setDate:_todayDate];
    _calendarManager.settings.weekModeEnabled = YES;
    [_calendarManager reload];
    
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
      if ([language isEqualToString:@"hebrew"]) {
        [_tabTitle setText:@"זמני תפילות"];
        [_dailyTimesTitle setText:@"תפילות היום"];
          [_shacharitLabel setText:@"שחרית"];
          [_michaLabel setText:@"מנחה"];
          [_maarivLabel setText:@"מעריב"];
     }
}

-(void)initData:(NSDate*)date{
    NSArray* nextPrayers = [[LogicServices sharedInstance] getNextPrayersForDate:date];
    [_firstPrayerTime setText:@""];
    [_secondPrayerTime setText:@""];
    [_thirdPrayerTime setText:@""];
    for (SmallPrayer* prayer in nextPrayers) {
        if ([prayer.type isEqualToString:@"morning"]) {
            if ([_firstPrayerTime.text isEqualToString:@""]) {
                [_firstPrayerTime setText:prayer.time];
            }
            else {
                [_firstPrayerTime setText:[NSString stringWithFormat:@"%@, %@", _firstPrayerTime.text, prayer.time]];
            }
        }
        if ([prayer.type isEqualToString:@"mincha"]) {
            if ([_secondPrayerTime.text isEqualToString:@""]) {
                [_secondPrayerTime setText:prayer.time];
            }
            else {
                [_secondPrayerTime setText:[NSString stringWithFormat:@"%@, %@", _secondPrayerTime.text, prayer.time]];
            }
        }
        if ([prayer.type isEqualToString:@"evening"]) {
            if ([_thirdPrayerTime.text isEqualToString:@""]) {
                [_thirdPrayerTime setText:prayer.time];
            }
            else {
                [_thirdPrayerTime setText:[NSString stringWithFormat:@"%@, %@", _thirdPrayerTime.text, prayer.time]];
            }
        }

    }
    
    PrayerDetailsModel* prayerDetails = [[LogicServices sharedInstance] getAllPrayersForDate:date];
    additions = prayerDetails.additions;
    smallPrayers = prayerDetails.prayers;
    [_additionsTable reloadData];
    _parashaName.text = prayerDetails.parashaName;
    if (![prayerDetails.holidayStart isEqualToString:@""]) {
    [_holidayStarTime setText:prayerDetails.holidayStart];
    [_holidayEndTime setText:prayerDetails.holidayEnd];
        [_holidayStartImage setHidden:NO];
        [_holidayEndImage setHidden:NO];
    }
    else {
        [_holidayStarTime setText:@""];
        [_holidayEndTime setText:@""];
        [_holidayStartImage setHidden:YES];
        [_holidayEndImage setHidden:YES];
    }
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
        dayView.dot2View.backgroundColor = [UIColor whiteColor];
        dayView.textLabel.textColor = [UIColor whiteColor];
    }
    // Other month
    else if(![_calendarManager.dateHelper date:_calendarContentView.date isTheSameMonthThan:dayView.date]){
        dayView.circleView.hidden = YES;
        dayView.dot2View.backgroundColor = [UIColor redColor];
        dayView.textLabel.textColor = [UIColor lightGrayColor];
    }
    // Another day of the current month
    else{
        dayView.circleView.hidden = YES;
        dayView.dot2View.backgroundColor = [UIColor redColor];
        dayView.textLabel.textColor = [UIColor blackColor];
    }
    
    if([self havePrayersForDay:dayView.date]){
        dayView.dot2View.hidden = NO;
    }
    else{
        dayView.dot2View.hidden = YES;
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
    
    [self initData:_dateSelected];
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
    _minDate = [_calendarManager.dateHelper addToDate:_todayDate months:0];
    
    // Max date will be 12 month after today
    _maxDate = [_calendarManager.dateHelper addToDate:_todayDate months:12];
}

// Used only to have a key for _eventsByDate
- (NSDateFormatter *)dateFormatter
{
    static NSDateFormatter *dateFormatter;
    if(!dateFormatter){
        dateFormatter = [NSDateFormatter new];
        dateFormatter.dateFormat = @"dd-MM-yyyy";
    }
    
    return dateFormatter;
}

- (BOOL)havePrayersForDay:(NSDate *)date
{
    dailyEventsModel* dailyEvents = [[LogicServices sharedInstance] getCommunityDailyEventsForDate:date];
    if (dailyEvents.prayers.count > 0) {
        if (![((PrayerDetailsModel*)dailyEvents.prayers[0]).holidayStart isEqualToString:@""] || ((PrayerDetailsModel*)dailyEvents.prayers[0]).prayers.count>0) {
                return YES;
        }
    }
    return NO;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if ([tableView.restorationIdentifier isEqualToString:@"additions"]) {
    return additions.count/2+additions.count%2;
    }
    if ([tableView.restorationIdentifier isEqualToString:@"smallPrayersTable"]) {
        return smallPrayers.count;
    }
    else {
    return popupServices.count;
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (![tableView.restorationIdentifier isEqualToString:@"additions"]) {
        if ([tableView.restorationIdentifier isEqualToString:@"smallPrayersTable"]) {
            ServiceCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ServiceCell"];
            if (cell == nil) {
                // Load the top-level objects from the custom cell XIB.
                NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"ServiceCell" owner:self options:nil];
                // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
                cell = [topLevelObjects objectAtIndex:0];
            }
            [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
            [cell.serviceTitle setText:((SmallPrayer*)smallPrayers[indexPath.row]).title];
            [cell.serviceTimes setText:((SmallPrayer*)smallPrayers[indexPath.row]).time];
            
            return cell;
        }
        else {
    ServiceCell *cell = [tableView dequeueReusableCellWithIdentifier:@"serviceCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"ServiceCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    [cell.serviceTitle setText:((SmallPrayer*)popupServices[indexPath.row]).title];
    [cell.serviceTimes setText:((SmallPrayer*)popupServices[indexPath.row]).time];
    
    return cell;
        }
    }
    else {
        AdditionCell *cell = [tableView dequeueReusableCellWithIdentifier:@"AdditionCell"];
        if (cell == nil) {
            // Load the top-level objects from the custom cell XIB.
            NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"AdditionCell" owner:self options:nil];
            // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
            cell = [topLevelObjects objectAtIndex:0];
        }
        [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
        [cell.addition1 setText:additions[indexPath.row*2]];
        if (additions.count>indexPath.row*2+1) {
        [cell.addition2 setText:additions[indexPath.row*2+1]];
        }
        
        return cell;
    }
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
     if (![tableView.restorationIdentifier isEqualToString:@"additions"]) {
    [_scrollView setScrollEnabled:YES];
    [UIView animateWithDuration:0.3 animations:^{
        [_popupView setAlpha:0];
        [_blackBG setAlpha:0];
    }];
     }
}
- (IBAction)hidePopupClicked {
    [_scrollView setScrollEnabled:YES];
    [UIView animateWithDuration:0.3 animations:^{
        [_popupView setAlpha:0];
        [_blackBG setAlpha:0];
    }];
}

- (IBAction)morningServiceClicked {
    [self showPopupWith:@"morning"];
}

- (IBAction)minchaServiceClicked {
    [self showPopupWith:@"mincha"];
}

- (IBAction)eveningServiceClicked {
    [self showPopupWith:@"evening"];
}

-(void)showPopupWith:(NSString*)serviceTime {
    NSArray* nextPrayers = [[LogicServices sharedInstance] getNextPrayersForDate:_dateSelected];
    popupServices = [NSMutableArray array];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];

    if ([serviceTime isEqualToString:@"morning"]) {
        if ([language isEqualToString:@"english"]) {
            [_popupTitle setText:@"Shacharit services"];
        }
        if ([language isEqualToString:@"hebrew"]) {
            [_popupTitle setText:@"מנייני שחרית"];
        }
        [_popupTitle setTextColor:[UIColor colorWithRed:139.0f/255.0f green:195.0f/255.0f blue:74.0f/255.0f alpha:1]];
        for (SmallPrayer* prayer in nextPrayers) {
            SmallPrayer* service = [[SmallPrayer alloc]init];
            if ([prayer.type isEqualToString:@"morning"]) {
                service.time = prayer.time;
                service.title = prayer.title;
                [popupServices addObject:service];
            }
        }
    }

    
    if ([serviceTime isEqualToString:@"mincha"]) {
        if ([language isEqualToString:@"english"]) {
        [_popupTitle setText:@"Mincha services"];
        }
        if ([language isEqualToString:@"hebrew"]) {
        [_popupTitle setText:@"מנייני מנחה"];
        }
                
        [_popupTitle setTextColor:[UIColor colorWithRed:246.0f/255.0f green:125.0f/255.0f blue:36.0f/255.0f alpha:1]];
        for (SmallPrayer* prayer in nextPrayers) {
            SmallPrayer* service = [[SmallPrayer alloc]init];
            if ([prayer.type isEqualToString:@"mincha"]) {
                service.time = prayer.time;
                service.title = prayer.title;
                [popupServices addObject:service];
            }
        }
    }

    
    if ([serviceTime isEqualToString:@"evening"]) {
        if ([language isEqualToString:@"english"]) {
            [_popupTitle setText:@"Marriv services"];
        }
                if ([language isEqualToString:@"hebrew"]) {
                    [_popupTitle setText:@"מנייני מעריב"];
                }
        [_popupTitle setTextColor:[UIColor colorWithRed:3.0f/255.0f green:169.0f/255.0f blue:244.0f/255.0f alpha:1]];
        for (SmallPrayer* prayer in nextPrayers) {
            SmallPrayer* service = [[SmallPrayer alloc]init];
            if ([prayer.type isEqualToString:@"evening"]) {
                service.time = prayer.time;
                service.title = prayer.title;
                [popupServices addObject:service];
            }
        }
    }
    [_scrollView setScrollEnabled:NO];
    [UIView animateWithDuration:0.3 animations:^{
        [_popupView setAlpha:1];
        [_blackBG setAlpha:0.5];
    }];
    
    [_servicesTable reloadData];
}

- (IBAction)backClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
