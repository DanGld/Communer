//
//  QuestionsAndAnswersViewController.h
//  Smart Community
//
//  Created by Jukk on 8/17/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QuestionsAndAnswersViewController : UIViewController <UITableViewDelegate, UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *questionsTable;
@property (strong, nonatomic) IBOutlet UILabel *privateTitle;
@property (strong, nonatomic) IBOutlet UIImageView *privateUnderline;
@property (strong, nonatomic) IBOutlet UILabel *publicTitle;
@property (strong, nonatomic) IBOutlet UIImageView *publicUnderline;
@property (strong, nonatomic) IBOutlet UIButton *privateButton;
@property (strong, nonatomic) IBOutlet UILabel *tabTitle;

- (IBAction)privateButtonClicked;
- (IBAction)publicButtonClicked;
- (IBAction)createQuestionClicked;

- (IBAction)backAction:(id)sender;

@end
