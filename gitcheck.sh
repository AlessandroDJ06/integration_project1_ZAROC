
#!/bin/bash

# Zorg dat we alle argumenten (zoals --since of --author) doorgeven
git_log_opts=( "$@" )

git log "${git_log_opts[@]}" --format='author: %ae' --numstat | \
    tr '[A-Z]' '[a-z]' | \
    grep -v '^$' | \
    grep -v '^-' | \
    awk '
    {
        if ($1 == "author:") {
            author = $2;
            commits[author]++;
        } else {
            insertions[author] += $1;
            deletions[author] += $2;
            total[author] += $1 + $2;
            
            author_file = author ":" $3;
            if (!(author_file in seen)) {
                seen[author_file] = 1;
                files[author]++;
            }
        }
    }
    END {
        printf("%-30s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n",
               "Email", "Commits", "Files",
               "Insertions", "Deletions", "Total Lines");
        printf("%-30s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n",
               "-----", "-------", "-----",
               "----------", "---------", "-----------");
        
        for (email in total) {
            printf("%-30s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n",
                   email, commits[email], files[email],
                   insertions[email], deletions[email], total[email]);
        }
    }'
