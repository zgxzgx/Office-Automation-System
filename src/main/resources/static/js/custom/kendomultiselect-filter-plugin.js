/**
 * Created by sqhe18 on 14-12-8.
 */
kendo.ui.plugin(kendo.ui.MultiSelect.extend({
    options: {
        name: "MultiFilterMultiSelect"
    },
    search: function(word) {

        var that = this,
            options = that.options,
            ignoreCase = options.ignoreCase,
            filter = options.filter,
            field = options.dataTextField,
            inputValue = that.input.val(),
            expression,
            length;
        //console.log("search",this)

        if (options.placeholder === inputValue) {
            inputValue = "";
        }

        clearTimeout(that._typing);

        word = typeof word === "string" ? word : inputValue;

        length = word.length;

        if (!length || length >= options.minLength) {
            that._state = "filter";
            that._open = true;

            var myFilters = options.searchFilters.map(function(temp){
                return { field: temp, operator: "contains", value: word };
            })

            this.dataSource.filter({
                logic: "or",
                filters: myFilters
            });
            that._retrieveData = false;
        }
    }
}));
