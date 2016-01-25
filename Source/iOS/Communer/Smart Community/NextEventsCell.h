//
//  NextEventsCell.h
//  Smart Community
//
//  Created by oren shany on 7/30/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <JTCalendar/JTCalendar.h>
#import "LogicServices.h"


@interface NextEventsCell : UITableViewCell<JTCalendarDelegate,UITableViewDataSource,UITableViewDelegate>

@property (weak, nonatomic) IBOutlet JTCalendarMenuView *calendarMenuView;
@property (weak, nonatomic) IBOutlet JTHorizontalCalendarView *calendarContentView;
@property (strong, nonatomic) IBOutlet UITableView *eventsTable;

@property (strong, nonatomic) JTCalendarManager *calendarManager;

@property (strong, nonatomic) UIButton *cellBtn;
- (IBAction)rightArrowClicked;
- (IBAction)leftArrowClicked;


@property (weak, nonatomic) IBOutlet NSLayoutConstraint *calendarContentViewHeight;

@end
