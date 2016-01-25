//
//  SingleEventViewController.h
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EventOrMessageModel.h"

@interface SingleEventViewController : UIViewController<UICollectionViewDataSource,UICollectionViewDelegate>

@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) EventOrMessageModel* event;
@property (strong, nonatomic) IBOutlet UILabel *eventDate;
@property (strong, nonatomic) IBOutlet UILabel *eventName;
@property (strong, nonatomic) IBOutlet UILabel *eventTime;
@property (strong, nonatomic) IBOutlet UITextView *eventContent;
@property (strong, nonatomic) IBOutlet UICollectionView *participantsCollection;
@property (weak, nonatomic) IBOutlet UIButton *yesCheckbox;
@property (weak, nonatomic) IBOutlet UIButton *maybeCheckbox;
@property (weak, nonatomic) IBOutlet UIButton *noCheckbox;
@property (strong, nonatomic) IBOutlet UIWebView *mapWebView;
@property (strong, nonatomic) IBOutlet UIButton *addToCalenderButton;
@property (strong, nonatomic) IBOutlet UIButton *attendingButton;
@property (strong, nonatomic) IBOutlet UILabel *participantsLabel;
@property (strong, nonatomic) IBOutlet UILabel *areYouAttendingLabel;
@property (strong, nonatomic) IBOutlet UIButton *popupCancelButton;
@property (strong, nonatomic) IBOutlet UIButton *popupOkButton;
@property (strong, nonatomic) IBOutlet UILabel *popupYesLabel;
@property (strong, nonatomic) IBOutlet UILabel *popupMaybeTitle;
@property (strong, nonatomic) IBOutlet UILabel *popupNoTitle;
- (IBAction)shareClicked;
- (IBAction)attendingClicked;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollContainer;
@property (weak, nonatomic) IBOutlet UILabel *blackBGLayer;
@property (strong, nonatomic) IBOutlet UILabel *noParticipantsLabel;

@property (weak, nonatomic) IBOutlet UIView *AttendingView;
- (IBAction)backBtn;
@property (strong, nonatomic) IBOutlet UIButton *buyTicketButton;
- (IBAction)buyTicketAction:(id)sender;
- (IBAction)openMaps;


@end
