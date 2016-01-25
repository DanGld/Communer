//
//  GuideViewController.m
//  Smart Community
//
//  Created by oren shany on 8/27/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "GuideViewController.h"
#import "UIImageView+AFNetworking.h"
#import "CommentCell.h"
#import "CommentModel.h"
#import "LogicServices.h"
#import "Networking.h"

@interface GuideViewController ()

@end

@implementation GuideViewController
NSMutableArray* comments;
double commentViewRate;

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupView];
    self.commentsTable.delegate = self;
    self.commentsTable.dataSource = self;
    self.texView.delegate = self;
}

-(void)generateFakeComments {
    comments = [NSMutableArray array];
    for (int i=0; i<5; i++) {
        CommentModel* comment = [[CommentModel alloc]init];
        comment.imageUrl = _serviceContact.imageUrl;
        comment.name = @"Shuby Dooby";
        comment.date = [[NSDate date]timeIntervalSince1970]*1000;
        comment.content = @"I just want to say how great the service was! HE IS THE BEST!!!";
        comment.rate = 3;
        [comments addObject:comment];
    }
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    comments = [NSMutableArray arrayWithArray:[comments sortedArrayUsingComparator: ^(CommentModel* p1, CommentModel *p2) {
        return [[[NSDate alloc]initWithTimeIntervalSince1970:p2.date/1000] compare:[[NSDate alloc]initWithTimeIntervalSince1970:p1.date/1000]];
    }]];
    return comments.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CommentCell *cell = [tableView dequeueReusableCellWithIdentifier:@"CommentCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"CommentCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    CommentModel* comment = comments[indexPath.row];
    [cell.commentName setText:comment.name];
    
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:comment.imageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [cell.commentImage setImageWithURLRequest:imageRequest
                       placeholderImage:[UIImage imageNamed:@"noprofilepic.jpg"]
                                success:nil
                                failure:nil];
    
    [cell setStars:comment.rate];
    [cell.contentLabel setText:comment.content];
    return cell;
}

-(void)textViewDidBeginEditing:(UITextView *)textView {
    [UIView animateWithDuration:0.3 animations:^{
        [_CommentView setFrame:CGRectMake(_CommentView.frame.origin.x, _CommentView.frame.origin.y-120, _CommentView.frame.size.width, _CommentView.frame.size.height)];
    }];
}

-(void)textViewDidEndEditing:(UITextView *)textView {
    [UIView animateWithDuration:0.3 animations:^{
        [_CommentView setFrame:CGRectMake(_CommentView.frame.origin.x, _CommentView.frame.origin.y+120, _CommentView.frame.size.width, _CommentView.frame.size.height)];
    }];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}


-(void)setupView {
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([appLanguage isEqualToString:@"english"]) {
        [_guideNumOfVotes setText: [NSString stringWithFormat:@"%lu Votes", (unsigned long)comments.count]];
    }
    if ([appLanguage isEqualToString:@"hebrew"]) {
        [_guideNumOfVotes setText: [NSString stringWithFormat:@"%lu הצבעות", (unsigned long)comments.count]];
        [_ratingLabel setText:@"דירוג"];
        [_rateLabel setText:@"דרג"];
        [_descLabel setText:@"תיאור"];
        [_cancelLabel setTitle:@"ביטול" forState:UIControlStateNormal];
        [_postLabel setTitle:@"פרסם" forState:UIControlStateNormal];
    }

    [_guideImage.layer setCornerRadius:18];
    [_guideImage.layer setMasksToBounds:YES];
    [_titleLabel setText:_guideTitle];
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:_serviceContact.imageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    
    [_guideImage setImageWithURLRequest:imageRequest
                             placeholderImage:[UIImage imageNamed:@"noprofilepic.jpg"]
                                      success:nil
                                      failure:nil];
    
    [_guideName setText:_serviceContact.name];
    [_guideAddress setText: _serviceContact.location.title];
    [_guidePhoneNumber setText:_serviceContact.phoneNumber];
    [_guideRating setText:[NSString stringWithFormat:@"%.1f" , _serviceContact.rating]];
    [self setStars];
}

-(void)setStars {
    if (_serviceContact.rating>1) {
        [_star1 setImage:[UIImage imageNamed:@"gray_staer"]];
    }
    if (_serviceContact.rating>2) {
        [_star2 setImage:[UIImage imageNamed:@"gray_staer"]];
    }
    if (_serviceContact.rating>3) {
        [_star3 setImage:[UIImage imageNamed:@"gray_staer"]];
    }
    if (_serviceContact.rating>4) {
        [_star4 setImage:[UIImage imageNamed:@"gray_staer"]];
    }
    if (_serviceContact.rating==5) {
        [_star5 setImage:[UIImage imageNamed:@"gray_staer"]];
    }
}

- (IBAction)backClicked {
    [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)CreateCommentClicked {
    [UIView animateWithDuration:0.3 animations:^{
        [_grayCoverView setAlpha:0.5];
        [_CommentView setAlpha:1];
    }];
}
- (IBAction)postCommentClicked {
    
    CommentModel* comment = [[CommentModel alloc]init];
    comment.imageUrl = [DataManagement sharedInstance].user.imageUrl;
    comment.name = [DataManagement sharedInstance].user.name;
    comment.date = [[NSDate date] timeIntervalSince1970]*1000;
    comment.content = _texView.text;
    comment.rate = commentViewRate;
    [comments addObject:comment];
    [_commentsTable reloadData];
    [[Networking sharedInstance] postNewComment:comment ForServiceContactID:_serviceContact.serviceContactID];
    //update counter
    [_guideNumOfVotes setText:[NSString stringWithFormat:@"%lu Votes",(unsigned long)comments.count]];
    
    
    [UIView animateWithDuration:0.3 animations:^{
        [_grayCoverView setAlpha:0];
        [_CommentView setAlpha:0];
    }];
    [_commentsTable scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:YES];
    [self.view endEditing:YES];
}

- (IBAction)cancelCommentClicked {
    [UIView animateWithDuration:0.3 animations:^{
        [_grayCoverView setAlpha:0];
        [_CommentView setAlpha:0];
    }];
    [self.view endEditing:YES];
}
- (IBAction)star1Clicked {
    [_commentStar1 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar2 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    [_commentStar3 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    [_commentStar4 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    [_commentStar5 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    commentViewRate=1;
}
- (IBAction)star2Clicked {
    [_commentStar1 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar2 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar3 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    [_commentStar4 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    [_commentStar5 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    commentViewRate=2;
}
- (IBAction)star3Clicked {
    [_commentStar1 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar2 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar3 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar4 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    [_commentStar5 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    commentViewRate=3;
}
- (IBAction)star4Clicked {
    [_commentStar1 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar2 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar3 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar4 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar5 setImage:[UIImage imageNamed:@"gray_star"] forState:UIControlStateNormal];
    commentViewRate=4;
}
- (IBAction)star5Clicked {
    [_commentStar1 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar2 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar3 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar4 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    [_commentStar5 setImage:[UIImage imageNamed:@"gray_staer"] forState:UIControlStateNormal];
    commentViewRate=5;
}




@end
