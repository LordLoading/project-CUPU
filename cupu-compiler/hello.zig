const std = @import("std");
const warn = std.debug.warn;
const allocator = std.debug.global_allocator;
const process = std.process;

pub fn main() !void {
    var arg_it = process.args();

    // skip my own exe name
    _ = arg_it.skip();

    std.debug.print(arg_it, .{});
}
