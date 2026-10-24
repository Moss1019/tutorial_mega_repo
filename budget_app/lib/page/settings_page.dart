import 'package:budget_app/provider/service_provider.dart';
import 'package:flutter/material.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({super.key});

  @override
  State<StatefulWidget> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  ThemeMode _selectedTheme = ThemeMode.system;

  Color _selectedColor = Colors.green;

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
          title: Text(
              "Settings",
              style: t.textTheme.headlineMedium?.copyWith(color: t.colorScheme.onPrimary)
          ),
          backgroundColor: t.colorScheme.primary,
        iconTheme: IconThemeData(
          color: t.colorScheme.onPrimary
        ),
      ),
      body: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              child: Padding(
                padding: EdgeInsetsGeometry.symmetric(vertical: 32),
                child: Column(
                  children: [
                    Text("Theme"),
                    DropdownMenu<ThemeMode>(
                      initialSelection: _selectedTheme,
                      onSelected: (val) {
                        setState(() {
                          _selectedTheme = val!;
                        });
                        final messageBus = ServiceProvider.messageBusOf(context);
                        messageBus.emit("themeMode", val!);
                      },
                      dropdownMenuEntries: [
                        DropdownMenuEntry(
                          value: ThemeMode.system,
                          label: "System",
                        ),
                        DropdownMenuEntry(
                          value: ThemeMode.light,
                          label: "Light",
                        ),
                        DropdownMenuEntry(value: ThemeMode.dark, label: "Dark"),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            Card(
              child: Padding(
                padding: EdgeInsetsGeometry.symmetric(vertical: 32),
                child: Column(
                  children: [
                    Text("Theme"),
                    DropdownMenu<Color>(
                      initialSelection: _selectedColor,
                      onSelected: (val) {
                        setState(() {
                          _selectedColor = val!;
                        });
                        final messageBus = ServiceProvider.messageBusOf(context);
                        messageBus.emit("color", val!);
                      },
                      dropdownMenuEntries: [
                        DropdownMenuEntry(value: Colors.red, label: "Red"),
                        DropdownMenuEntry(value: Colors.blue, label: "Blue"),
                        DropdownMenuEntry(value: Colors.green, label: "Green"),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
