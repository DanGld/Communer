//
//  QuestionCommentCellTableViewCell.h
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CommentModel.h"

@interface QuestionCommentCellTableViewCell : UITableViewCell

@property (strong, nonatomic) IBOutlet UIImageView *commentImage;
@property (strong, nonatomic) IBOutlet UILabel *commentName;
@property (strong, nonatomic) IBOutlet UILabel *commentDate;
@property (strong, nonatomic) IBOutlet UILabel *commentContent;

- (void)initWithComment:(CommentModel *)comment;

@end
