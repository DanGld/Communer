//
//  CalenderController.h
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <JTCalendar/JTCalendar.h>
#import "LogicServices.h"
#import "CalenderEventCell.h"
#import "MFSideMenu.h"
#import "SingleEventViewController.h"
#import "VCFloatingActionButton.h"

@interface CalenderController : UIViewController<JTCalendarDelegate,UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) IBOutlet UILabel *tabTitle;
@property (weak, nonatomic) IBOutlet JTCalendarMenuView *calendarMenuView;
@property (weak, nonatomic) IBOutlet JTHorizontalCalendarView *calendarContentView;

@property (strong, nonatomic) JTCalendarManager *calendarManager;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *calendarContentViewHeight;
@property (strong, nonatomic) IBOutlet UITableView *eventsTable;
@property (strong, nonatomic) IBOutlet UIImageView *communityImage;
@property (strong, nonatomic) IBOutlet UILabel *communityTitle;
@property (strong, nonatomic) IBOutlet UILabel *communityLocation;
@property(nonatomic)MFSideMenuContainerViewController *container;
@property (strong, nonatomic) IBOutlet UIButton *filterButton;

@property (strong, nonatomic) NSDate *selectedDayFromHome;

- (IBAction)menuAction:(id)sender;

- (IBAction)leftArrowClicked;
- (IBAction)rightArrowClicked;

@end
