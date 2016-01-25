//
//  PrayersTimeController.h
//  Smart Community
//
//  Created by oren shany on 8/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LogicServices.h"
#import "ServiceCell.h"
#import "AdditionCell.h"
#import "FacilityOrActivityModel.h"
#import "MFSideMenu.h"
#import <JTCalendar/JTCalendar.h>

@interface PrayersTimeController : UIViewController<UITableViewDelegate,UITableViewDataSource,JTCalendarDelegate>
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;
@property (strong, nonatomic) IBOutlet UISegmentedControl *segment;
@property (strong, nonatomic) IBOutlet UIView *prayersView;
@property (strong, nonatomic) IBOutlet UILabel *firstPrayerTime;
@property (strong, nonatomic) IBOutlet UILabel *secondPrayerTime;
@property (strong, nonatomic) IBOutlet UILabel *thirdPrayerTime;
@property (strong, nonatomic) IBOutlet UILabel *parashaName;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayer1;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayer2;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayer3;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayer4;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayer5;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayer6;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayerTime1;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayerTime2;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayerTime3;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayerTime4;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayerTime5;
@property (strong, nonatomic) IBOutlet UILabel *smallPrayerTime6;
@property(nonatomic)MFSideMenuContainerViewController *container;
@property (weak, nonatomic) IBOutlet UITableView *servicesTable;
@property (weak, nonatomic) IBOutlet UITableView *smallPrayersTable;
@property (weak, nonatomic) IBOutlet UILabel *popupTitle;
@property (weak, nonatomic) IBOutlet UILabel *holidayStarTime;
@property (weak, nonatomic) IBOutlet UILabel *holidayEndTime;
@property (weak, nonatomic) IBOutlet UIImageView *holidayStartImage;
@property (weak, nonatomic) IBOutlet UIImageView *holidayEndImage;
@property (strong, nonatomic) IBOutlet UILabel *tabTitle;
@property (strong, nonatomic) IBOutlet UILabel *dailyTimesTitle;
@property (strong, nonatomic) IBOutlet UILabel *shacharitLabel;
@property (strong, nonatomic) IBOutlet UILabel *michaLabel;
@property (strong, nonatomic) IBOutlet UILabel *maarivLabel;

@property (weak, nonatomic) IBOutlet JTHorizontalCalendarView *calendarContentView;
@property (weak, nonatomic) IBOutlet JTCalendarMenuView *calendarMenuView;
@property (strong, nonatomic) JTCalendarManager *calendarManager;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *calendarContentViewHeight;
@property (weak, nonatomic) IBOutlet UIView *popupView;
@property (weak, nonatomic) IBOutlet UIButton *blackBG;
@property (weak, nonatomic) IBOutlet UITableView *additionsTable;
- (IBAction)morningServiceClicked;
- (IBAction)minchaServiceClicked;
- (IBAction)eveningServiceClicked;

@end
