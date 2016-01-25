//
//  GuideViewController.h
//  Smart Community
//
//  Created by oren shany on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServiceContactModel.h"

@interface GuideViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UITextViewDelegate>
@property (strong, nonatomic) ServiceContactModel* serviceContact;
@property (strong, nonatomic) NSString *guideTitle;
@property (strong, nonatomic) IBOutlet UILabel *titleLabel;
@property (strong, nonatomic) IBOutlet UIImageView *guideImage;
@property (strong, nonatomic) IBOutlet UILabel *guideName;
@property (strong, nonatomic) IBOutlet UILabel *guideAddress;
@property (strong, nonatomic) IBOutlet UILabel *guidePhoneNumber;
@property (strong, nonatomic) IBOutlet UILabel *guideRating;
@property (strong, nonatomic) IBOutlet UILabel *guideNumOfVotes;
@property (strong, nonatomic) IBOutlet UIImageView *star1;
@property (strong, nonatomic) IBOutlet UIImageView *star2;
@property (strong, nonatomic) IBOutlet UIImageView *star3;
@property (strong, nonatomic) IBOutlet UIImageView *star4;
@property (strong, nonatomic) IBOutlet UIImageView *star5;
@property (strong, nonatomic) IBOutlet UITableView *commentsTable;
@property (weak, nonatomic) IBOutlet UITextView *texView;
@property (weak, nonatomic) IBOutlet UIView *CommentView;
@property (weak, nonatomic) IBOutlet UILabel *grayCoverView;
@property (weak, nonatomic) IBOutlet UIButton *commentStar1;
@property (weak, nonatomic) IBOutlet UIButton *commentStar2;
@property (weak, nonatomic) IBOutlet UIButton *commentStar3;
@property (weak, nonatomic) IBOutlet UIButton *commentStar4;
@property (weak, nonatomic) IBOutlet UIButton *commentStar5;
@property (strong, nonatomic) IBOutlet UILabel *ratingLabel;
@property (strong, nonatomic) IBOutlet UILabel *rateLabel;
@property (strong, nonatomic) IBOutlet UILabel *descLabel;
@property (strong, nonatomic) IBOutlet UIButton *cancelLabel;
@property (strong, nonatomic) IBOutlet UIButton *postLabel;

-(void)setupView;
- (IBAction)backClicked;
- (IBAction)CreateCommentClicked;
- (IBAction)postCommentClicked;
- (IBAction)cancelCommentClicked;
@end
