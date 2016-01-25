//
//  ImageModel.h
//  Smart Community
//
//  Created by Eyal Makover on 10/24/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KVCBaseObject.h"

@interface ImageModel : KVCBaseObject

@property (nonatomic, strong) NSString* imageID;
@property (nonatomic, strong) NSString* link;
@property (nonatomic, strong) NSMutableArray* comments;

@end
